package com.josbar.medisistemas.mappers.impl;

import com.josbar.medisistemas.domain.dtos.cita.CitaRequestDTO;
import com.josbar.medisistemas.domain.dtos.cita.CitaResponseDTO;
import com.josbar.medisistemas.domain.entities.CitaEntity;
import com.josbar.medisistemas.domain.entities.MedicoEntity;
import com.josbar.medisistemas.domain.entities.PacienteEntity;
import com.josbar.medisistemas.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CitaMapper implements Mapper<CitaEntity, CitaRequestDTO, CitaResponseDTO> {
    private final MedicoMapper medicoMapper;
    private final PacienteMapper pacienteMapper;
    private final CatalogoMapper catalogoMapper;

    @Override
    public CitaEntity toEntity(CitaRequestDTO request) {
        if(request == null) return null;
        CitaEntity entity = new CitaEntity();

        MedicoEntity medico = new MedicoEntity();
        medico.setId(request.getIdMedico());
        entity.setMedicoEntity(medico);

        PacienteEntity paciente = new PacienteEntity();
        paciente.setId(request.getIdPaciente());
        entity.setPacienteEntity(paciente);

        entity.setFecha(request.getFecha());
        entity.setHora(request.getHora());
        return entity;
    }

    @Override
    public CitaResponseDTO toResponse(CitaEntity entity) {
        if(entity == null) return null;
        CitaResponseDTO dto = new CitaResponseDTO();
        dto.setId(entity.getId());
        dto.setFecha(entity.getFecha());
        dto.setHora(entity.getHora());
        dto.setHoraLlegada(entity.getHoraLlegada());
        dto.setHoraSolicitudLlamado(entity.getHoraSolicitudLlamado());
        dto.setMedicoResponseDTO(medicoMapper.toResponse(entity.getMedicoEntity()));
        dto.setPacienteResponseDTO(pacienteMapper.toResponse(entity.getPacienteEntity()));
        dto.setEstadoCitaResponseDTO(catalogoMapper.toResponse(entity.getEstadoCitaEntity()));
        return dto;
    }
}