package com.josbar.medisistemas.repositories;

import com.josbar.medisistemas.domain.entities.UsuarioEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<UsuarioEntity, Integer> {
    Optional<UsuarioEntity> findByCorreo(String correo);
}
