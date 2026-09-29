package com.josbar.medisistemas.mappers.impl;

import com.josbar.medisistemas.domain.dtos.documento.ActualizarDocumentoRequestDTO;
import com.josbar.medisistemas.domain.dtos.documento.DocumentoResponseDTO;
import com.josbar.medisistemas.domain.dtos.documento.SubirDocumentoRequestDTO;
import com.josbar.medisistemas.domain.entities.CategoriaDocumentoEntity;
import com.josbar.medisistemas.domain.entities.DocumentoEntity;
import com.josbar.medisistemas.domain.entities.PacienteEntity;
import com.josbar.medisistemas.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentoMapper implements Mapper<DocumentoEntity, SubirDocumentoRequestDTO, DocumentoResponseDTO> {

    private final CatalogoMapper catalogoMapper;

    @Override
    public DocumentoEntity toEntity(SubirDocumentoRequestDTO request) {
        if(request == null) return null;
        DocumentoEntity entity = new DocumentoEntity();

        PacienteEntity paciente = new PacienteEntity();
        paciente.setId(request.getIdPaciente());
        entity.setPacienteEntity(paciente);

        CategoriaDocumentoEntity categoria = new CategoriaDocumentoEntity();
        categoria.setId(request.getIdCategoriaDocumento());
        entity.setCategoriaDocumentoEntity(categoria);

        entity.setNombre(request.getNombre());
        entity.setUrl(request.getUrl());

        // Temporal: mientras no exista JWT, idUsuarioCarga se recibe explícitamente en el Request.
        if (request.getIdUsuarioCarga() != null) {
            var usuarioCarga = new com.josbar.medisistemas.domain.entities.UsuarioEntity();
            usuarioCarga.setId(request.getIdUsuarioCarga());
            entity.setUsuarioEntityCarga(usuarioCarga);
        }
        return entity;
    }

    @Override
    public DocumentoResponseDTO toResponse(DocumentoEntity entity) {
        if(entity == null) return null;
        DocumentoResponseDTO dto = new DocumentoResponseDTO();
        dto.setId(entity.getId());
        dto.setIdPaciente(entity.getPacienteEntity() != null ? entity.getPacienteEntity().getId() : null);
        dto.setIdUsuarioCarga(entity.getUsuarioEntityCarga() != null ? entity.getUsuarioEntityCarga().getId() : null);
        dto.setNombre(entity.getNombre());
        dto.setUrl(entity.getUrl());
        dto.setFechaCarga(entity.getFechaCarga());
        dto.setCategoriaDocumentoResponseDTO(catalogoMapper.toResponse(entity.getCategoriaDocumentoEntity()));
        return dto;
    }

    public void updateEntity(ActualizarDocumentoRequestDTO request, DocumentoEntity entity) {
        if (request == null || entity == null) return;

        if (request.getNombre() != null && !request.getNombre().trim().isEmpty()) {
            entity.setNombre(request.getNombre());
        }
        if (request.getUrl() != null && !request.getUrl().trim().isEmpty()) {
            entity.setUrl(request.getUrl());
        }
    }
}