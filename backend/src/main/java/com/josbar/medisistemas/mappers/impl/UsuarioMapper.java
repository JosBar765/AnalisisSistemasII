package com.josbar.medisistemas.mappers.impl;

import com.josbar.medisistemas.domain.dtos.usuario.UsuarioRequestDTO;
import com.josbar.medisistemas.domain.dtos.usuario.UsuarioResponseDTO;
import com.josbar.medisistemas.domain.entities.RolEntity;
import com.josbar.medisistemas.domain.entities.UsuarioEntity;
import com.josbar.medisistemas.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.josbar.medisistemas.utils.Texto;

@Component
@RequiredArgsConstructor
public class UsuarioMapper implements Mapper<UsuarioEntity, UsuarioRequestDTO, UsuarioResponseDTO> {

    private final CatalogoMapper catalogoMapper; // Asumiendo que sabe mapear un Rol

    @Override
    public UsuarioEntity toEntity(UsuarioRequestDTO request) {
        if (request == null) return null;

        UsuarioEntity entity = new UsuarioEntity();
        RolEntity rolEntity = new RolEntity();
        rolEntity.setId(request.getIdRol());
        entity.setRolEntity(rolEntity);

        entity.setPrimerNombre(request.getPrimerNombre());
        entity.setSegundoNombre(request.getSegundoNombre());
        entity.setPrimerApellido(request.getPrimerApellido());
        entity.setSegundoApellido(request.getSegundoApellido());
        entity.setCorreo(request.getCorreo());
        entity.setTelefono(request.getTelefono());
        entity.setContrasenia(request.getContrasenia());
        entity.setEstado(request.getEstado());

        return entity;
    }

    @Override
    public UsuarioResponseDTO toResponse(UsuarioEntity entity) {
        if (entity == null) return null;

        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(entity.getId());
        response.setPrimerNombre(Texto.titulo(entity.getPrimerNombre()));
        response.setSegundoNombre(Texto.titulo(entity.getSegundoNombre()));
        response.setPrimerApellido(Texto.titulo(entity.getPrimerApellido()));
        response.setSegundoApellido(Texto.titulo(entity.getSegundoApellido()));
        response.setCorreo(entity.getCorreo());
        response.setTelefono(entity.getTelefono());
        response.setEstado(entity.getEstado());
        response.setFechaCreacion(entity.getFechaCreacion());
        response.setRol(catalogoMapper.toResponse(entity.getRolEntity()));
        // Contraseña excluida obligatoriamente
        return response;
    }

    public void updateEntity(UsuarioRequestDTO request, UsuarioEntity entity) {
        if (request == null || entity == null) return;

        if (request.getPrimerNombre() != null) entity.setPrimerNombre(request.getPrimerNombre());
        if (request.getSegundoNombre() != null) entity.setSegundoNombre(request.getSegundoNombre());
        if (request.getPrimerApellido() != null) entity.setPrimerApellido(request.getPrimerApellido());
        if (request.getSegundoApellido() != null) entity.setSegundoApellido(request.getSegundoApellido());
        if (request.getCorreo() != null) entity.setCorreo(request.getCorreo());
        if (request.getTelefono() != null) entity.setTelefono(request.getTelefono());
        if (request.getEstado() != null) entity.setEstado(request.getEstado());

        // El Rol se maneja con cuidado para no instanciar si no viene en el Request
        if (request.getIdRol() != null) {
            RolEntity rol = new RolEntity();
            rol.setId(request.getIdRol());
            entity.setRolEntity(rol);
        }
    }
}