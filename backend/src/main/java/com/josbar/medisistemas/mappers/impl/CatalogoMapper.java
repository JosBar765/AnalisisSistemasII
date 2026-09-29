package com.josbar.medisistemas.mappers.impl;

import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoRequestDTO;
import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoResponseDTO;
import com.josbar.medisistemas.domain.entities.*;
import com.josbar.medisistemas.mappers.Mapper;
import org.springframework.stereotype.Component;

/**
 * Mapper genérico para entidades tipo catálogo (id + un único campo de texto):
 * Rol, Especialidad, DiaSemana, EstadoCita, CategoriaDocumento,
 * MotivoModificacionConsulta y MotivoModificacionDocumento.
 * La creación/edición de cada catálogo la resuelve su propio Service,
 * ya que cada entidad tiene su propio constructor y nombre de columna.
 */
@Component
public class CatalogoMapper implements Mapper<Object, CatalogoRequestDTO, CatalogoResponseDTO> {

    @Override
    public Object toEntity(CatalogoRequestDTO request) {
        throw new UnsupportedOperationException("Cada catálogo construye su propia entidad en su Service.");
    }

    @Override
    public CatalogoResponseDTO toResponse(Object entity) {
        if (entity == null) return null;

        if (entity instanceof RolEntity rol) {
            return CatalogoResponseDTO.builder().id(rol.getId()).nombre(rol.getRol()).build();
        }
        if (entity instanceof EspecialidadEntity especialidad) {
            return CatalogoResponseDTO.builder().id(especialidad.getId()).nombre(especialidad.getEspecialidad()).build();
        }
        if (entity instanceof DiaSemanaEntity diaSemana) {
            return CatalogoResponseDTO.builder().id(diaSemana.getId()).nombre(diaSemana.getDiaSemana()).build();
        }
        if (entity instanceof EstadoCitaEntity estadoCita) {
            return CatalogoResponseDTO.builder().id(estadoCita.getId()).nombre(estadoCita.getEstadoCita()).build();
        }
        if (entity instanceof CategoriaDocumentoEntity categoria) {
            return CatalogoResponseDTO.builder().id(categoria.getId()).nombre(categoria.getCategoriaDocumento()).build();
        }
        if (entity instanceof MotivoModificacionConsultaEntity motivo) {
            return CatalogoResponseDTO.builder().id(motivo.getId()).nombre(motivo.getMotivoModificacion()).build();
        }
        if (entity instanceof MotivoModificacionDocumentoEntity motivo) {
            return CatalogoResponseDTO.builder().id(motivo.getId()).nombre(motivo.getMotivoModificacion()).build();
        }

        throw new IllegalArgumentException("Tipo de catálogo no soportado: " + entity.getClass().getSimpleName());
    }
}
