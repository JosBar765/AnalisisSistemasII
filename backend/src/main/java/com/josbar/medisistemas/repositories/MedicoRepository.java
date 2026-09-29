package com.josbar.medisistemas.repositories;

import com.josbar.medisistemas.domain.entities.MedicoEntity;
import org.springframework.data.repository.CrudRepository;

public interface MedicoRepository extends CrudRepository<MedicoEntity, Integer> {
    boolean existsByEspecialidadId(Integer idEspecialidad);
}
