package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.CitaService;
import com.josbar.medisistemas.domain.dtos.cita.HorarioDisponibleResponseDTO;
import com.josbar.medisistemas.domain.entities.CitaEntity;
import com.josbar.medisistemas.domain.entities.DiaSemanaEntity;
import com.josbar.medisistemas.domain.entities.JornadaMedicaEntity;
import com.josbar.medisistemas.exceptions.ResourceNotFoundException;
import com.josbar.medisistemas.repositories.CitaRepository;
import com.josbar.medisistemas.repositories.DiaSemanaRepository;
import com.josbar.medisistemas.repositories.EstadoCitaRepository;
import com.josbar.medisistemas.repositories.JornadaMedicaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * CRUD mínimo para citas (fuera del alcance del módulo Admin, ver
 * .agents/backend/documentacion/creacion_modulo_admin.md). La disponibilidad
 * de horarios se calcula combinando la jornada del médico y las citas ya
 * registradas, tal como describe UC-SEC-003, sin implementar la cola diaria
 * de atención (regla de negocio del módulo Médico, no solicitada aquí).
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

    public CitaServiceImpl(CitaRepository citaRepository, JornadaMedicaRepository jornadaMedicaRepository,
                            DiaSemanaRepository diaSemanaRepository, EstadoCitaRepository estadoCitaRepository) {
        this.citaRepository = citaRepository;
        this.jornadaMedicaRepository = jornadaMedicaRepository;
        this.diaSemanaRepository = diaSemanaRepository;
        this.estadoCitaRepository = estadoCitaRepository;
    }

    @Override
    @Transactional
    public CitaEntity programar(CitaEntity entity) {
        entity.setEstadoCitaEntity(estadoCitaRepository.findByEstadoCita(ESTADO_EN_ESPERA)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el estado de cita '" + ESTADO_EN_ESPERA + "'.")));
        return citaRepository.save(entity);
    }

    @Override
    public List<HorarioDisponibleResponseDTO> obtenerHorariosDisponibles(Integer idMedico, LocalDate fecha) {
        String nombreDia = DIA_SEMANA_ES.get(fecha.getDayOfWeek());
        DiaSemanaEntity diaSemana = diaSemanaRepository.findByDiaSemanaIgnoreCase(nombreDia).orElse(null);
        if (diaSemana == null) {
            return List.of();
        }

        List<JornadaMedicaEntity> jornadas = jornadaMedicaRepository.findByMedicoEntityIdAndDiaSemanaEntityId(idMedico, diaSemana.getId());
        if (jornadas.isEmpty()) {
            return List.of();
        }

        Set<LocalTime> horasOcupadas = citaRepository.findByMedicoEntityIdAndFechaAndEstadoCitaEntityEstadoCitaNot(idMedico, fecha, ESTADO_CANCELADO)
                .stream()
                .map(CitaEntity::getHora)
                .collect(Collectors.toSet());

        List<HorarioDisponibleResponseDTO> disponibles = new ArrayList<>();
        for (JornadaMedicaEntity jornada : jornadas) {
            LocalTime horaActual = jornada.getHoraInicio();
            while (horaActual.plusMinutes(jornada.getDuracionConsulta()).compareTo(jornada.getHoraFin()) <= 0) {
                if (!horasOcupadas.contains(horaActual)) {
                    disponibles.add(HorarioDisponibleResponseDTO.builder().hora(horaActual).build());
                }
                horaActual = horaActual.plusMinutes(jornada.getDuracionConsulta());
            }
        }
        return disponibles;
    }

    @Override
    public List<CitaEntity> obtenerAgendaDiaria(LocalDate fecha) {
        return citaRepository.findByFecha(fecha);
    }

    @Override
    public List<CitaEntity> obtenerAgendaPorMedico(Integer idMedico, LocalDate fecha) {
        return citaRepository.findByMedicoEntityIdAndFecha(idMedico, fecha);
    }

    @Override
    @Transactional
    public CitaEntity cancelar(Integer id) {
        CitaEntity entity = findById(id);
        entity.setEstadoCitaEntity(estadoCitaRepository.findByEstadoCita(ESTADO_CANCELADO)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el estado de cita '" + ESTADO_CANCELADO + "'.")));
        return citaRepository.save(entity);
    }

    @Override
    @Transactional
    public CitaEntity reprogramar(Integer id, CitaEntity nuevaInformacion) {
        CitaEntity entity = findById(id);
        entity.setFecha(nuevaInformacion.getFecha());
        entity.setHora(nuevaInformacion.getHora());
        return citaRepository.save(entity);
    }

    private CitaEntity findById(Integer id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la cita con id " + id));
    }
}
