package com.josbar.medisistemas.repositories;

import com.josbar.medisistemas.domain.entities.JornadaMedicaEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JornadaMedicaRepository extends CrudRepository<JornadaMedicaEntity, Integer> {
    List<JornadaMedicaEntity> findByMedicoEntityId(Integer idMedico);
    List<JornadaMedicaEntity> findByMedicoEntityIdAndDiaSemanaEntityId(Integer idMedico, Integer idDiaSemana);
}
