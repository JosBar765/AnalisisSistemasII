package com.josbar.medisistemas.mappers.impl;

import com.josbar.medisistemas.domain.dtos.jornada_medica.JornadaMedicaRequestDTO;
import com.josbar.medisistemas.domain.dtos.jornada_medica.JornadaMedicaResponseDTO;
import com.josbar.medisistemas.domain.entities.DiaSemanaEntity;
import com.josbar.medisistemas.domain.entities.JornadaMedicaEntity;
import com.josbar.medisistemas.domain.entities.MedicoEntity;
import com.josbar.medisistemas.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JornadaMedicaMapper implements Mapper<JornadaMedicaEntity, JornadaMedicaRequestDTO, JornadaMedicaResponseDTO> {
    private final CatalogoMapper catalogoMapper;

    @Override
    public JornadaMedicaEntity toEntity(JornadaMedicaRequestDTO request) {
        if(request == null) return null;
        JornadaMedicaEntity entity = new JornadaMedicaEntity();

        MedicoEntity medico = new MedicoEntity();
        medico.setId(request.getIdMedico());
        entity.setMedicoEntity(medico);

        DiaSemanaEntity dia = new DiaSemanaEntity();
        dia.setId(request.getIdDiaSemana());
        entity.setDiaSemanaEntity(dia);

        entity.setHoraInicio(request.getHoraInicio());
        entity.setHoraFin(request.getHoraFin());
        entity.setDuracionConsulta(request.getDuracionConsulta());
        return entity;
    }

    @Override
    public JornadaMedicaResponseDTO toResponse(JornadaMedicaEntity entity) {
        if(entity == null) return null;
        JornadaMedicaResponseDTO dto = new JornadaMedicaResponseDTO();
        dto.setId(entity.getId());
        dto.setHoraInicio(entity.getHoraInicio());
        dto.setHoraFin(entity.getHoraFin());
        dto.setDuracionConsulta(entity.getDuracionConsulta());
        dto.setDiaSemanaResponseDTO(catalogoMapper.toResponse(entity.getDiaSemanaEntity()));
        return dto;
    }
}