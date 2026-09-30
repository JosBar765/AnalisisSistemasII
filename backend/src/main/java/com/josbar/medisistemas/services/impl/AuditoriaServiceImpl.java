package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.AuditoriaService;
import com.josbar.medisistemas.domain.dtos.auditoria.AuditoriaConsultaResponseDTO;
import com.josbar.medisistemas.domain.dtos.auditoria.AuditoriaDocumentoResponseDTO;
import com.josbar.medisistemas.mappers.impl.AuditoriaConsultaMapper;
import com.josbar.medisistemas.mappers.impl.AuditoriaDocumentoMapper;
import com.josbar.medisistemas.repositories.AuditoriaConsultaRepository;
import com.josbar.medisistemas.repositories.AuditoriaDocumentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaConsultaRepository auditoriaConsultaRepository;
    private final AuditoriaDocumentoRepository auditoriaDocumentoRepository;
    private final AuditoriaConsultaMapper auditoriaConsultaMapper;
    private final AuditoriaDocumentoMapper auditoriaDocumentoMapper;

    public AuditoriaServiceImpl(AuditoriaConsultaRepository auditoriaConsultaRepository, AuditoriaDocumentoRepository auditoriaDocumentoRepository,
                                 AuditoriaConsultaMapper auditoriaConsultaMapper, AuditoriaDocumentoMapper auditoriaDocumentoMapper) {
        this.auditoriaConsultaRepository = auditoriaConsultaRepository;
        this.auditoriaDocumentoRepository = auditoriaDocumentoRepository;
        this.auditoriaConsultaMapper = auditoriaConsultaMapper;
        this.auditoriaDocumentoMapper = auditoriaDocumentoMapper;
    }

    @Override
    public List<AuditoriaConsultaResponseDTO> obtenerAuditoriasPorConsulta(Integer idConsulta) {
        return auditoriaConsultaRepository.findByConsultaEntityIdOrderByFechaModificacionDesc(idConsulta).stream()
                .map(auditoriaConsultaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditoriaDocumentoResponseDTO> obtenerAuditoriasDocumentosPorPaciente(Integer idPaciente) {
        return auditoriaDocumentoRepository.findByDocumentoEntityPacienteEntityIdOrderByFechaModificacionDesc(idPaciente).stream()
                .map(auditoriaDocumentoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditoriaDocumentoResponseDTO> obtenerAuditoriasPorDocumento(Integer idDocumento) {
        return auditoriaDocumentoRepository.findByDocumentoEntityId(idDocumento).stream()
                .map(auditoriaDocumentoMapper::toResponse)
                .collect(Collectors.toList());
    }
}
