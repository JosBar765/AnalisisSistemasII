package com.josbar.medisistemas.repositories;

import com.josbar.medisistemas.domain.entities.EstadoCitaEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EstadoCitaRepository extends CrudRepository<EstadoCitaEntity, Integer> {
    Optional<EstadoCitaEntity> findByEstadoCita(String estadoCita);
}
