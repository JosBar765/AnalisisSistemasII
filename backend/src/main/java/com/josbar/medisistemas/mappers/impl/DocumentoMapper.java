package com.josbar.medisistemas.mappers.impl;

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

    /** Solo referencia paciente y categoría; el nombre, la URL y el usuario de carga los asigna el Service. */
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
}
