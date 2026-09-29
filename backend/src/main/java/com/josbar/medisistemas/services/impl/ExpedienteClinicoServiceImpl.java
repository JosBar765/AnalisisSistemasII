package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.ExpedienteClinicoService;
import com.josbar.medisistemas.domain.dtos.expediente_clinico.ExpedienteClinicoResponseDTO;
import com.josbar.medisistemas.exceptions.ResourceNotFoundException;
import com.josbar.medisistemas.mappers.impl.ConsultaMapper;
import com.josbar.medisistemas.mappers.impl.DocumentoMapper;
import com.josbar.medisistemas.mappers.impl.PacienteMapper;
import com.josbar.medisistemas.repositories.ConsultaRepository;
import com.josbar.medisistemas.repositories.DocumentoRepository;
import com.josbar.medisistemas.repositories.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ExpedienteClinicoServiceImpl implements ExpedienteClinicoService {

    private final PacienteRepository pacienteRepository;
    private final ConsultaRepository consultaRepository;
    private final DocumentoRepository documentoRepository;
    private final PacienteMapper pacienteMapper;
    private final ConsultaMapper consultaMapper;
    private final DocumentoMapper documentoMapper;

    public ExpedienteClinicoServiceImpl(PacienteRepository pacienteRepository, ConsultaRepository consultaRepository,
                                         DocumentoRepository documentoRepository, PacienteMapper pacienteMapper,
                                         ConsultaMapper consultaMapper, DocumentoMapper documentoMapper) {
        this.pacienteRepository = pacienteRepository;
        this.consultaRepository = consultaRepository;
        this.documentoRepository = documentoRepository;
        this.pacienteMapper = pacienteMapper;
        this.consultaMapper = consultaMapper;
        this.documentoMapper = documentoMapper;
    }

    @Override
    public ExpedienteClinicoResponseDTO obtenerExpediente(Integer idPaciente) {
        var paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el paciente con id " + idPaciente));

        return ExpedienteClinicoResponseDTO.builder()
                .pacienteResponseDTO(pacienteMapper.toResponse(paciente))
                .historialConsultasResponseDTO(consultaRepository.findByCitaEntityPacienteEntityId(idPaciente).stream()
                        .map(consultaMapper::toResponse)
                        .collect(Collectors.toList()))
                .documentosClinicosResponseDTO(documentoRepository.findByPacienteEntityId(idPaciente).stream()
                        .map(documentoMapper::toResponse)
                        .collect(Collectors.toList()))
                .build();
    }
}
