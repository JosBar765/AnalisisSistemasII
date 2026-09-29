package com.josbar.medisistemas.mappers.impl;

import com.josbar.medisistemas.domain.dtos.auditoria.AuditoriaConsultaResponseDTO;
import com.josbar.medisistemas.domain.entities.AuditoriaConsultaEntity;
import com.josbar.medisistemas.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaConsultaMapper implements Mapper<AuditoriaConsultaEntity, Void, AuditoriaConsultaResponseDTO> {

    @Override
    public AuditoriaConsultaEntity toEntity(Void request) {
        throw new UnsupportedOperationException("Las auditorías son inmutables y no se crean desde un Request");
    }

    @Override
    public AuditoriaConsultaResponseDTO toResponse(AuditoriaConsultaEntity entity) {
        if(entity == null) return null;

        AuditoriaConsultaResponseDTO dto = new AuditoriaConsultaResponseDTO();
        dto.setId(entity.getId());

        // Extracción de IDs de las relaciones (Consulta, Usuario, Motivo)
        dto.setIdConsulta(
                entity.getConsultaEntity() != null
                        ? entity.getConsultaEntity().getId()
                        : null
        );
        dto.setIdUsuario(
                entity.getUsuarioEntity() != null
                        ? entity.getUsuarioEntity().getId()
                        : null
        );
        dto.setIdMotivoModificacion(
                entity.getMotivoModificacionConsultaEntity() != null
                        ? entity.getMotivoModificacionConsultaEntity().getId()
                        : null
        );

        // Textos clínicos anteriores
        dto.setMotivoConsultaAnterior(entity.getMotivoConsultaAnterior());
        dto.setDiagnosticoAnterior(entity.getDiagnosticoAnterior());
        dto.setTratamientoAnterior(entity.getTratamientoAnterior());
        dto.setObservacionesAnterior(entity.getObservacionesAnterior());

        // Textos clínicos nuevos
        dto.setMotivoConsultaNuevo(entity.getMotivoConsultaNuevo());
        dto.setDiagnosticoNuevo(entity.getDiagnosticoNuevo());
        dto.setTratamientoNuevo(entity.getTratamientoNuevo());
        dto.setObservacionesNuevo(entity.getObservacionesNuevo());

        // Fecha de la auditoría
        dto.setFechaModificacion(entity.getFechaModificacion());

        return dto;
    }
}