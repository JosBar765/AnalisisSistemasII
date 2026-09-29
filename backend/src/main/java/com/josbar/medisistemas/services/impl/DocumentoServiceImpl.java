package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.DocumentoService;
import com.josbar.medisistemas.domain.dtos.documento.ActualizarDocumentoRequestDTO;
import com.josbar.medisistemas.domain.entities.DocumentoEntity;
import com.josbar.medisistemas.exceptions.ResourceNotFoundException;
import com.josbar.medisistemas.mappers.impl.DocumentoMapper;
import com.josbar.medisistemas.repositories.DocumentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CRUD mínimo para documentos clínicos (fuera del alcance del módulo Admin,
 * ver .agents/backend/documentacion/creacion_modulo_admin.md). No implementa
 * generación de auditoría de actualizaciones: esa regla pertenece al módulo
 * de Secretaría (UC-SEC-002), no solicitado en esta fase.
 */
@Service
public class DocumentoServiceImpl implements DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final DocumentoMapper documentoMapper;

    public DocumentoServiceImpl(DocumentoRepository documentoRepository, DocumentoMapper documentoMapper) {
        this.documentoRepository = documentoRepository;
        this.documentoMapper = documentoMapper;
    }

    @Override
    @Transactional
    public DocumentoEntity subir(DocumentoEntity entity) {
        entity.setFechaCarga(LocalDateTime.now());
        return documentoRepository.save(entity);
    }

    @Override
    @Transactional
    public DocumentoEntity actualizar(Integer id, ActualizarDocumentoRequestDTO request) {
        DocumentoEntity entity = documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el documento con id " + id));
        documentoMapper.updateEntity(request, entity);
        return documentoRepository.save(entity);
    }

    @Override
    public List<DocumentoEntity> listarPorPaciente(Integer idPaciente) {
        return documentoRepository.findByPacienteEntityId(idPaciente);
    }
}
