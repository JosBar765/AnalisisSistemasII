package com.josbar.medisistemas.repositories;

import com.josbar.medisistemas.domain.entities.DiaSemanaEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DiaSemanaRepository extends CrudRepository<DiaSemanaEntity, Integer> {
    Optional<DiaSemanaEntity> findByDiaSemanaIgnoreCase(String diaSemana);
}
