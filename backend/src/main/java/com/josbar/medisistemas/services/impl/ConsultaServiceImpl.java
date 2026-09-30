package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.domain.dtos.cita.EventoCitaDTO;
import com.josbar.medisistemas.domain.dtos.cita.TipoEventoCita;
import com.josbar.medisistemas.domain.dtos.consulta.ModificarConsultaRequestDTO;
import com.josbar.medisistemas.domain.entities.AuditoriaConsultaEntity;
import com.josbar.medisistemas.domain.entities.CitaEntity;
import com.josbar.medisistemas.domain.entities.ConsultaEntity;
import com.josbar.medisistemas.domain.entities.MotivoModificacionConsultaEntity;
import com.josbar.medisistemas.domain.entities.SignosVitalesEntity;
import com.josbar.medisistemas.domain.entities.UsuarioEntity;
import com.josbar.medisistemas.exceptions.BusinessRuleException;
import com.josbar.medisistemas.exceptions.ResourceNotFoundException;
import com.josbar.medisistemas.mappers.impl.ConsultaMapper;
import com.josbar.medisistemas.repositories.AuditoriaConsultaRepository;
import com.josbar.medisistemas.repositories.CitaRepository;
import com.josbar.medisistemas.repositories.ConsultaRepository;
import com.josbar.medisistemas.repositories.EstadoCitaRepository;
import com.josbar.medisistemas.repositories.MotivoModificacionConsultaRepository;
import com.josbar.medisistemas.services.CitaEventoPublisher;
import com.josbar.medisistemas.services.ConsultaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Registro y modificación de consultas médicas (UC-MED-003 y UC-MED-004). Solo el médico dueño de la
 * cita registra o corrige la consulta. Las consultas no se eliminan y toda modificación deja una
 * auditoría con la información anterior y la nueva (incluidos los signos vitales).
 */
@Service
public class ConsultaServiceImpl implements ConsultaService {

    private static final String ESTADO_EN_ESPERA = "En espera";
    private static final String ESTADO_ATENDIDO = "Atendido";

    private final ConsultaRepository consultaRepository;
    private final CitaRepository citaRepository;
    private final EstadoCitaRepository estadoCitaRepository;
    private final AuditoriaConsultaRepository auditoriaRepository;
    private final MotivoModificacionConsultaRepository motivoRepository;
    private final ConsultaMapper consultaMapper;
    private final CitaEventoPublisher eventoPublisher;

    public ConsultaServiceImpl(ConsultaRepository consultaRepository, CitaRepository citaRepository,
                                EstadoCitaRepository estadoCitaRepository, AuditoriaConsultaRepository auditoriaRepository,
                                MotivoModificacionConsultaRepository motivoRepository, ConsultaMapper consultaMapper,
                                CitaEventoPublisher eventoPublisher) {
        this.consultaRepository = consultaRepository;
        this.citaRepository = citaRepository;
        this.estadoCitaRepository = estadoCitaRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.motivoRepository = motivoRepository;
        this.consultaMapper = consultaMapper;
        this.eventoPublisher = eventoPublisher;
    }

    @Override
    @Transactional
    public ConsultaEntity registrar(ConsultaEntity entity, Integer idMedico) {
        CitaEntity cita = citaRepository.findById(entity.getCitaEntity().getId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la cita asociada a la consulta."));
        if (!cita.getMedicoEntity().getId().equals(idMedico)) {
            throw new BusinessRuleException("El médico solo puede registrar la consulta de sus propias citas.");
        }
        if (!ESTADO_EN_ESPERA.equals(cita.getEstadoCitaEntity().getEstadoCita())) {
            throw new BusinessRuleException("Solo se puede registrar la consulta de una cita en estado '" + ESTADO_EN_ESPERA + "'.");
        }
        if (consultaRepository.existsByCitaEntityId(cita.getId())) {
            throw new BusinessRuleException("La cita ya tiene una consulta registrada.");
        }

        entity.setCitaEntity(cita);
        ConsultaEntity guardada = consultaRepository.save(entity);

        cita.setEstadoCitaEntity(estadoCitaRepository.findByEstadoCita(ESTADO_ATENDIDO)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el estado de cita '" + ESTADO_ATENDIDO + "'.")));
        citaRepository.save(cita);
        eventoPublisher.publicar(new EventoCitaDTO(TipoEventoCita.CITA_ATENDIDA, cita.getId(), idMedico));
        return guardada;
    }

    @Override
    public ConsultaEntity findById(Integer id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la consulta con id " + id));
    }

    @Override
    public List<ConsultaEntity> listarDelMedico(Integer idMedico, LocalDate fecha) {
        return consultaRepository.findByCitaEntityMedicoEntityIdAndCitaEntityFechaOrderByCitaEntityHora(idMedico, fecha);
    }

    @Override
    @Transactional
    public ConsultaEntity modificar(Integer id, ModificarConsultaRequestDTO request, Integer idMedico) {
        ConsultaEntity entity = findById(id);
        if (!entity.getCitaEntity().getMedicoEntity().getId().equals(idMedico)) {
            throw new BusinessRuleException("Solo el médico que atendió la consulta puede modificarla.");
        }
        MotivoModificacionConsultaEntity motivo = motivoRepository.findById(request.getIdMotivoModificacionConsulta())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el motivo de modificación indicado."));

        AuditoriaConsultaEntity auditoria = capturarAnterior(entity);
        consultaMapper.updateEntity(request, entity);
        capturarNuevo(auditoria, entity);
        if (!hayCambios(auditoria)) {
            throw new BusinessRuleException("No se detectaron cambios respecto a la información registrada.");
        }

        UsuarioEntity medico = new UsuarioEntity();
        medico.setId(idMedico);
        auditoria.setConsultaEntity(entity);
        auditoria.setUsuarioEntity(medico);
        auditoria.setMotivoModificacionConsultaEntity(motivo);
        auditoria.setFechaModificacion(LocalDateTime.now());
        auditoriaRepository.save(auditoria);
        return consultaRepository.save(entity);
    }

    private AuditoriaConsultaEntity capturarAnterior(ConsultaEntity entity) {
        AuditoriaConsultaEntity auditoria = new AuditoriaConsultaEntity();
        auditoria.setMotivoConsultaAnterior(entity.getMotivoConsulta());
        auditoria.setDiagnosticoAnterior(entity.getDiagnostico());
        auditoria.setTratamientoAnterior(entity.getTratamiento());
        auditoria.setObservacionesAnterior(entity.getObservaciones());
        SignosVitalesEntity sv = entity.getSignosVitalesEntity();
        if (sv != null) {
            auditoria.setPesoAnterior(sv.getPeso());
            auditoria.setAlturaAnterior(sv.getAltura());
            auditoria.setPresionSistolicaAnterior(sv.getPresionSistolica());
            auditoria.setPresionDiastolicaAnterior(sv.getPresionDiastolica());
            auditoria.setTemperaturaAnterior(sv.getTemperatura());
        }
        return auditoria;
    }

    private void capturarNuevo(AuditoriaConsultaEntity auditoria, ConsultaEntity entity) {
        auditoria.setMotivoConsultaNuevo(entity.getMotivoConsulta());
        auditoria.setDiagnosticoNuevo(entity.getDiagnostico());
        auditoria.setTratamientoNuevo(entity.getTratamiento());
        auditoria.setObservacionesNuevo(entity.getObservaciones());
        SignosVitalesEntity sv = entity.getSignosVitalesEntity();
        if (sv != null) {
            auditoria.setPesoNuevo(sv.getPeso());
            auditoria.setAlturaNueva(sv.getAltura());
            auditoria.setPresionSistolicaNueva(sv.getPresionSistolica());
            auditoria.setPresionDiastolicaNueva(sv.getPresionDiastolica());
            auditoria.setTemperaturaNueva(sv.getTemperatura());
        }
    }

    private boolean hayCambios(AuditoriaConsultaEntity a) {
        return !Objects.equals(a.getMotivoConsultaAnterior(), a.getMotivoConsultaNuevo())
                || !Objects.equals(a.getDiagnosticoAnterior(), a.getDiagnosticoNuevo())
                || !Objects.equals(a.getTratamientoAnterior(), a.getTratamientoNuevo())
                || !Objects.equals(a.getObservacionesAnterior(), a.getObservacionesNuevo())
                || !mismoValor(a.getPesoAnterior(), a.getPesoNuevo())
                || !mismoValor(a.getAlturaAnterior(), a.getAlturaNueva())
                || !Objects.equals(a.getPresionSistolicaAnterior(), a.getPresionSistolicaNueva())
                || !Objects.equals(a.getPresionDiastolicaAnterior(), a.getPresionDiastolicaNueva())
                || !mismoValor(a.getTemperaturaAnterior(), a.getTemperaturaNueva());
    }

    private boolean mismoValor(BigDecimal anterior, BigDecimal nuevo) {
        return anterior == null || nuevo == null ? anterior == nuevo : anterior.compareTo(nuevo) == 0;
    }
}
