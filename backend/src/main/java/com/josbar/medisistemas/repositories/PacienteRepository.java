package com.josbar.medisistemas.repositories;

import com.josbar.medisistemas.domain.entities.PacienteEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PacienteRepository extends CrudRepository<PacienteEntity, Integer> {
    Optional<PacienteEntity> findByDpi(String dpi);
}
