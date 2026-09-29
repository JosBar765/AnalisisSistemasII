package com.josbar.medisistemas.mappers.impl;

import com.josbar.medisistemas.domain.dtos.medico.MedicoRequestDTO;
import com.josbar.medisistemas.domain.dtos.medico.MedicoResponseDTO;
import com.josbar.medisistemas.domain.entities.EspecialidadEntity;
import com.josbar.medisistemas.domain.entities.MedicoEntity;
import com.josbar.medisistemas.domain.entities.UsuarioEntity;
import com.josbar.medisistemas.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MedicoMapper implements Mapper<MedicoEntity, MedicoRequestDTO, MedicoResponseDTO> {
    private final UsuarioMapper usuarioMapper;
    private final CatalogoMapper catalogoMapper;

    @Override
    public MedicoEntity toEntity(MedicoRequestDTO request) {
        if(request == null) return null;
        MedicoEntity entity = new MedicoEntity();
        entity.setColegiado(request.getColegiado());

        UsuarioEntity usuarioEntity = new UsuarioEntity();
        usuarioEntity.setId(request.getIdUsuario());
        entity.setUsuarioEntity(usuarioEntity);

        EspecialidadEntity especialidadEntity = new EspecialidadEntity();
        especialidadEntity.setId(request.getIdEspecialidad());
        entity.setEspecialidad(especialidadEntity);

        return entity;
    }

    @Override
    public MedicoResponseDTO toResponse(MedicoEntity entity) {
        if(entity == null) return null;
        MedicoResponseDTO dto = new MedicoResponseDTO();
        dto.setId(entity.getId());
        dto.setColegiado(entity.getColegiado());
        dto.setUsuarioResponseDTO(usuarioMapper.toResponse(entity.getUsuarioEntity()));
        dto.setEspecialidadResponseDTO(catalogoMapper.toResponse(entity.getEspecialidad()));
        return dto;
    }
}