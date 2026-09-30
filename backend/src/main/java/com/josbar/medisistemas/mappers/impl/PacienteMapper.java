package com.josbar.medisistemas.mappers.impl;

import com.josbar.medisistemas.domain.dtos.paciente.PacienteRequestDTO;
import com.josbar.medisistemas.domain.dtos.paciente.PacienteResponseDTO;
import com.josbar.medisistemas.domain.entities.PacienteEntity;
import com.josbar.medisistemas.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper implements Mapper<PacienteEntity, PacienteRequestDTO, PacienteResponseDTO> {

    @Override
    public PacienteEntity toEntity(PacienteRequestDTO request) {
        if(request == null) return null;
        PacienteEntity entity = new PacienteEntity();
        entity.setDpi(request.getDpi());
        entity.setPrimerNombre(request.getPrimerNombre());
        entity.setSegundoNombre(request.getSegundoNombre());
        entity.setPrimerApellido(request.getPrimerApellido());
        entity.setSegundoApellido(request.getSegundoApellido());
        entity.setTelefono(request.getTelefono());
        entity.setCorreo(request.getCorreo());
        entity.setDireccion(request.getDireccion());
        entity.setFechaNacimiento(request.getFechaNacimiento());
        entity.setEstado(request.getEstado());
        return entity;
    }

    @Override
    public PacienteResponseDTO toResponse(PacienteEntity entity) {
        if(entity == null) return null;
        PacienteResponseDTO dto = new PacienteResponseDTO();
        dto.setId(entity.getId());
        dto.setDpi(entity.getDpi());
        dto.setPrimerNombre(entity.getPrimerNombre());
        dto.setSegundoNombre(entity.getSegundoNombre());
        dto.setPrimerApellido(entity.getPrimerApellido());
        dto.setSegundoApellido(entity.getSegundoApellido());
        dto.setTelefono(entity.getTelefono());
        dto.setCorreo(entity.getCorreo());
        dto.setDireccion(entity.getDireccion());
        dto.setFechaNacimiento(entity.getFechaNacimiento());
        dto.setEstado(entity.getEstado());
        return dto;
    }

    public void updateEntity(PacienteRequestDTO request, PacienteEntity entity) {
        if (request == null || entity == null) return;

        if (request.getPrimerNombre() != null) entity.setPrimerNombre(request.getPrimerNombre());
        if (request.getSegundoNombre() != null) entity.setSegundoNombre(request.getSegundoNombre());
        if (request.getPrimerApellido() != null) entity.setPrimerApellido(request.getPrimerApellido());
        if (request.getSegundoApellido() != null) entity.setSegundoApellido(request.getSegundoApellido());
        if (request.getFechaNacimiento() != null) entity.setFechaNacimiento(request.getFechaNacimiento());
        if (request.getTelefono() != null) entity.setTelefono(request.getTelefono());
        if (request.getCorreo() != null) entity.setCorreo(request.getCorreo());
        if (request.getDireccion() != null) entity.setDireccion(request.getDireccion());
        if (request.getEstado() != null) entity.setEstado(request.getEstado());
    }
}