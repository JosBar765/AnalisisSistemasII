package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.ConsultaService;
import com.josbar.medisistemas.domain.dtos.consulta.ModificarConsultaRequestDTO;
import com.josbar.medisistemas.domain.entities.CitaEntity;
import com.josbar.medisistemas.domain.entities.ConsultaEntity;
import com.josbar.medisistemas.exceptions.ResourceNotFoundException;
import com.josbar.medisistemas.mappers.impl.ConsultaMapper;
import com.josbar.medisistemas.repositories.CitaRepository;
import com.josbar.medisistemas.repositories.ConsultaRepository;
import com.josbar.medisistemas.repositories.EstadoCitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD mínimo para consultas médicas (fuera del alcance del módulo Admin,
 * ver .agents/backend/documentacion/creacion_modulo_admin.md). No genera
 * auditoría de modificaciones: esa regla pertenece al módulo Médico (UC-MED-004),
 * no solicitado en esta fase.
 */
@Service
public class ConsultaServiceImpl implements ConsultaService {

    private static final String ESTADO_ATENDIDO = "Atendido";

    private final ConsultaRepository consultaRepository;
    private final CitaRepository citaRepository;
    private final EstadoCitaRepository estadoCitaRepository;
    private final ConsultaMapper consultaMapper;

    public ConsultaServiceImpl(ConsultaRepository consultaRepository, CitaRepository citaRepository,
                                EstadoCitaRepository estadoCitaRepository, ConsultaMapper consultaMapper) {
        this.consultaRepository = consultaRepository;
        this.citaRepository = citaRepository;
        this.estadoCitaRepository = estadoCitaRepository;
        this.consultaMapper = consultaMapper;
    }

    @Override
    @Transactional
    public ConsultaEntity registrar(ConsultaEntity entity) {
        ConsultaEntity saved = consultaRepository.save(entity);

        CitaEntity cita = citaRepository.findById(saved.getCitaEntity().getId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la cita asociada a la consulta."));
        cita.setEstadoCitaEntity(estadoCitaRepository.findByEstadoCita(ESTADO_ATENDIDO)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el estado de cita '" + ESTADO_ATENDIDO + "'.")));
        citaRepository.save(cita);

        return saved;
    }

    @Override
    public ConsultaEntity findById(Integer id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la consulta con id " + id));
    }

    @Override
    @Transactional
    public ConsultaEntity modificar(Integer id, ModificarConsultaRequestDTO request) {
        ConsultaEntity entity = findById(id);
        consultaMapper.updateEntity(request, entity);
        return consultaRepository.save(entity);
    }
}
