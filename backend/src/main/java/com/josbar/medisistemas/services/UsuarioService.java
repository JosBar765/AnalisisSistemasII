package com.josbar.medisistemas.services;

import com.josbar.medisistemas.domain.dtos.usuario.UsuarioRequestDTO;
import com.josbar.medisistemas.domain.entities.UsuarioEntity;

import java.util.List;

public interface UsuarioService {
    UsuarioEntity save(UsuarioEntity entity);
    List<UsuarioEntity> findAll();
    UsuarioEntity findById(Integer id);
    UsuarioEntity modificar(Integer id, UsuarioRequestDTO request);
    UsuarioEntity cambiarEstado(Integer id, Boolean estado);
    void cambiarContrasenia(Integer id, String nuevaContrasenia);
}
