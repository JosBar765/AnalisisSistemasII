package com.josbar.medisistemas.mappers.impl;

import com.josbar.medisistemas.domain.dtos.auditoria.AuditoriaDocumentoResponseDTO;
import com.josbar.medisistemas.domain.entities.AuditoriaDocumentoEntity;
import com.josbar.medisistemas.mappers.Mapper;
import org.springframework.stereotype.Component;
import com.josbar.medisistemas.utils.Texto;

@Component
public class AuditoriaDocumentoMapper implements Mapper<AuditoriaDocumentoEntity, Void, AuditoriaDocumentoResponseDTO> {

    @Override
    public AuditoriaDocumentoEntity toEntity(Void request) {
        throw new UnsupportedOperationException("Las auditorías son inmutables y no se crean desde un Request");
    }

    @Override
    public AuditoriaDocumentoResponseDTO toResponse(AuditoriaDocumentoEntity entity) {
        if(entity == null) return null;

        AuditoriaDocumentoResponseDTO dto = new AuditoriaDocumentoResponseDTO();
        dto.setId(entity.getId());

        // Extracción de IDs de las relaciones (Documento, Usuario, Motivo)
        dto.setIdDocumento(
                entity.getDocumentoEntity() != null
                        ? entity.getDocumentoEntity().getId()
                        : null
        );
        dto.setIdUsuario(
                entity.getUsuarioEntity() != null
                        ? entity.getUsuarioEntity().getId()
                        : null
        );
        dto.setIdMotivoModificacionDocumento(
                entity.getMotivoModificacionDocumentoEntity() != null
                        ? entity.getMotivoModificacionDocumentoEntity().getId()
                        : null
        );

        if (entity.getUsuarioEntity() != null) {
            dto.setNombreUsuario(Texto.titulo(entity.getUsuarioEntity().getPrimerNombre() + " " + entity.getUsuarioEntity().getPrimerApellido()));
        }
        if (entity.getMotivoModificacionDocumentoEntity() != null) {
            dto.setMotivoModificacion(entity.getMotivoModificacionDocumentoEntity().getMotivoModificacion());
        }

        // Textos de documentos anteriores y nuevos
        dto.setNombreAnterior(entity.getNombreAnterior());
        dto.setUrlAnterior(entity.getUrlAnterior());
        dto.setNombreNuevo(entity.getNombreNuevo());
        dto.setUrlNuevo(entity.getUrlNuevo());

        // Fecha de la auditoría
        dto.setFechaModificacion(entity.getFechaModificacion());

        return dto;
    }
}