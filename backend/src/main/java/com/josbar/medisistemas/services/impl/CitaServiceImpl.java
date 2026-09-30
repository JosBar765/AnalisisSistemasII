package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.CitaEventoPublisher;
import com.josbar.medisistemas.services.CitaService;
import com.josbar.medisistemas.domain.dtos.cita.EventoCitaDTO;
import com.josbar.medisistemas.domain.dtos.cita.HorarioDisponibleResponseDTO;
import com.josbar.medisistemas.domain.dtos.cita.TipoEventoCita;
import com.josbar.medisistemas.domain.entities.CitaEntity;
import com.josbar.medisistemas.domain.entities.DiaSemanaEntity;
import com.josbar.medisistemas.domain.entities.JornadaMedicaEntity;
import com.josbar.medisistemas.domain.entities.MedicoEntity;
import com.josbar.medisistemas.domain.entities.PacienteEntity;
import com.josbar.medisistemas.exceptions.BusinessRuleException;
import com.josbar.medisistemas.exceptions.ResourceNotFoundException;
import com.josbar.medisistemas.repositories.CitaRepository;
import com.josbar.medisistemas.repositories.DiaSemanaRepository;
import com.josbar.medisistemas.repositories.EstadoCitaRepository;
import com.josbar.medisistemas.repositories.JornadaMedicaRepository;
import com.josbar.medisistemas.repositories.MedicoRepository;
import com.josbar.medisistemas.repositories.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Gestión de citas (UC-SEC-003). Los horarios disponibles se calculan combinando la jornada
 * del médico, su duración de consulta y las citas ya registradas. La hora de la cita es solo un
 * horario estimado: el orden real de atención lo decide el médico según los pacientes presentes
 * (los que tienen la llegada registrada).
 */
@Service
public class CitaServiceImpl implements CitaService {

    private static final String ESTADO_EN_ESPERA = "En espera";
    private static final String ESTADO_CANCELADO = "Cancelado";

    private static final Map<DayOfWeek, String> DIA_SEMANA_ES = Map.of(
            DayOfWeek.MONDAY, "Lunes",
            DayOfWeek.TUESDAY, "Martes",
            DayOfWeek.WEDNESDAY, "Miercoles",
            DayOfWeek.THURSDAY, "Jueves",
            DayOfWeek.FRIDAY, "Viernes",
            DayOfWeek.SATURDAY, "Sabado",
            DayOfWeek.SUNDAY, "Domingo"
    );

    private final CitaRepository citaRepository;
    private final JornadaMedicaRepository jornadaMedicaRepository;
    private final DiaSemanaRepository diaSemanaRepository;
    private final EstadoCitaRepository estadoCitaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final CitaEventoPublisher eventoPublisher;

    public CitaServiceImpl(CitaRepository citaRepository, JornadaMedicaRepository jornadaMedicaRepository,
                            DiaSemanaRepository diaSemanaRepository, EstadoCitaRepository estadoCitaRepository,
                            PacienteRepository pacienteRepository, MedicoRepository medicoRepository,
                            CitaEventoPublisher eventoPublisher) {
        this.citaRepository = citaRepository;
        this.jornadaMedicaRepository = jornadaMedicaRepository;
        this.diaSemanaRepository = diaSemanaRepository;
        this.estadoCitaRepository = estadoCitaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.eventoPublisher = eventoPublisher;
    }

    @Override
    @Transactional
    public CitaEntity programar(CitaEntity entity) {
        PacienteEntity paciente = pacienteRepository.findById(entity.getPacienteEntity().getId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el paciente indicado."));
        if (!Boolean.TRUE.equals(paciente.getEstado())) {
            throw new BusinessRuleException("No se puede programar una cita para un paciente inactivo.");
        }

        MedicoEntity medico = findMedico(entity.getMedicoEntity().getId());
        validarHorarioDisponible(medico, entity.getFecha(), entity.getHora(), null);

        entity.setPacienteEntity(paciente);
        entity.setMedicoEntity(medico);
        entity.setEstadoCitaEntity(estadoCitaRepository.findByEstadoCita(ESTADO_EN_ESPERA)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el estado de cita '" + ESTADO_EN_ESPERA + "'.")));
        CitaEntity guardada = citaRepository.save(entity);
        publicar(TipoEventoCita.CITA_CREADA, guardada);
        return guardada;
    }

    @Override
    public List<HorarioDisponibleResponseDTO> obtenerHorariosDisponibles(Integer idMedico, LocalDate fecha) {
        return calcularHorariosDisponibles(findMedico(idMedico), fecha, null).stream()
                .map(hora -> HorarioDisponibleResponseDTO.builder().hora(hora).build())
                .collect(Collectors.toList());
    }

    @Override
    public List<CitaEntity> obtenerAgendaDiaria(LocalDate fecha) {
        return citaRepository.findByFechaOrderByHora(fecha);
    }

    @Override
    public List<CitaEntity> obtenerAgendaPorRango(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new BusinessRuleException("La fecha inicial no puede ser posterior a la fecha final.");
        }
        return citaRepository.findByFechaBetweenOrderByFechaAscHoraAsc(desde, hasta);
    }

    @Override
    public List<CitaEntity> obtenerAgendaPorMedico(Integer idMedico, LocalDate fecha) {
        return citaRepository.findByMedicoEntityIdAndFechaOrderByHora(idMedico, fecha);
    }

    @Override
    @Transactional
    public CitaEntity cancelar(Integer id) {
        CitaEntity entity = findEnEspera(id, "cancelar");
        entity.setEstadoCitaEntity(estadoCitaRepository.findByEstadoCita(ESTADO_CANCELADO)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el estado de cita '" + ESTADO_CANCELADO + "'.")));
        CitaEntity guardada = citaRepository.save(entity);
        publicar(TipoEventoCita.CITA_CANCELADA, guardada);
        return guardada;
    }

    @Override
    @Transactional
    public CitaEntity reprogramar(Integer id, CitaEntity nuevaInformacion) {
        CitaEntity entity = findEnEspera(id, "reprogramar");
        validarHorarioDisponible(entity.getMedicoEntity(), nuevaInformacion.getFecha(), nuevaInformacion.getHora(), id);

        entity.setFecha(nuevaInformacion.getFecha());
        entity.setHora(nuevaInformacion.getHora());
        entity.setHoraLlegada(null);
        entity.setHoraSolicitudLlamado(null);
        CitaEntity guardada = citaRepository.save(entity);
        publicar(TipoEventoCita.CITA_ACTUALIZADA, guardada);
        return guardada;
    }

    @Override
    @Transactional
    public CitaEntity registrarLlegada(Integer id) {
        CitaEntity entity = findEnEspera(id, "registrar la llegada de");
        if (!LocalDate.now().equals(entity.getFecha())) {
            throw new BusinessRuleException("Solo se puede registrar la llegada de citas programadas para hoy.");
        }
        if (entity.getHoraLlegada() != null) {
            throw new BusinessRuleException("La llegada del paciente ya fue registrada.");
        }

        entity.setHoraLlegada(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        CitaEntity guardada = citaRepository.save(entity);
        publicar(TipoEventoCita.CITA_ACTUALIZADA, guardada);
        return guardada;
    }

    @Override
    @Transactional
    public CitaEntity solicitarLlamado(Integer id, Integer idMedico) {
        CitaEntity entity = findEnEspera(id, "solicitar el llamado de");
        if (!entity.getMedicoEntity().getId().equals(idMedico)) {
            throw new BusinessRuleException("El médico solo puede solicitar el llamado de pacientes de sus propias citas.");
        }
        if (entity.getHoraLlegada() == null) {
            throw new BusinessRuleException("Solo se puede solicitar el llamado de un paciente presente en la clínica.");
        }

        entity.setHoraSolicitudLlamado(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        CitaEntity guardada = citaRepository.save(entity);
        publicar(TipoEventoCita.CITA_ACTUALIZADA, guardada);
        return guardada;
    }

    private void validarHorarioDisponible(MedicoEntity medico, LocalDate fecha, LocalTime hora, Integer idCitaExcluida) {
        if (!Boolean.TRUE.equals(medico.getUsuarioEntity().getEstado())) {
            throw new BusinessRuleException("Un médico inactivo no puede recibir nuevas citas.");
        }
        if (!calcularHorariosDisponibles(medico, fecha, idCitaExcluida).contains(hora)) {
            throw new BusinessRuleException("El horario seleccionado no está disponible para el médico en la fecha indicada.");
        }
    }

    private List<LocalTime> calcularHorariosDisponibles(MedicoEntity medico, LocalDate fecha, Integer idCitaExcluida) {
        if (!Boolean.TRUE.equals(medico.getUsuarioEntity().getEstado())) {
            return List.of();
        }

        String nombreDia = DIA_SEMANA_ES.get(fecha.getDayOfWeek());
        DiaSemanaEntity diaSemana = diaSemanaRepository.findByDiaSemanaIgnoreCase(nombreDia).orElse(null);
        if (diaSemana == null) {
            return List.of();
        }

        List<JornadaMedicaEntity> jornadas = jornadaMedicaRepository
                .findByMedicoEntityIdAndDiaSemanaEntityId(medico.getId(), diaSemana.getId());
        if (jornadas.isEmpty()) {
            return List.of();
        }

        Set<LocalTime> horasOcupadas = citaRepository
                .findByMedicoEntityIdAndFechaAndEstadoCitaEntityEstadoCitaNot(medico.getId(), fecha, ESTADO_CANCELADO)
                .stream()
                .filter(cita -> !cita.getId().equals(idCitaExcluida))
                .map(CitaEntity::getHora)
                .collect(Collectors.toSet());

        List<LocalTime> disponibles = new ArrayList<>();
        for (JornadaMedicaEntity jornada : jornadas) {
            LocalTime horaActual = jornada.getHoraInicio();
            while (horaActual.plusMinutes(jornada.getDuracionConsulta()).compareTo(jornada.getHoraFin()) <= 0) {
                if (!horasOcupadas.contains(horaActual)) {
                    disponibles.add(horaActual);
                }
                horaActual = horaActual.plusMinutes(jornada.getDuracionConsulta());
            }
        }
        disponibles.sort(LocalTime::compareTo);
        return disponibles;
    }

    private void publicar(TipoEventoCita tipo, CitaEntity cita) {
        eventoPublisher.publicar(new EventoCitaDTO(tipo, cita.getId(), cita.getMedicoEntity().getId()));
    }

    private CitaEntity findEnEspera(Integer id, String accion) {
        CitaEntity entity = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la cita con id " + id));
        if (!ESTADO_EN_ESPERA.equals(entity.getEstadoCitaEntity().getEstadoCita())) {
            throw new BusinessRuleException("Solo se puede " + accion + " una cita en estado '" + ESTADO_EN_ESPERA + "'.");
        }
        return entity;
    }

    private MedicoEntity findMedico(Integer idMedico) {
        return medicoRepository.findById(idMedico)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el médico con id " + idMedico));
    }
}
