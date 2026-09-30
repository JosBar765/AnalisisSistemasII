package com.josbar.medisistemas.repositories;

import com.josbar.medisistemas.domain.entities.AuditoriaConsultaEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuditoriaConsultaRepository extends CrudRepository<AuditoriaConsultaEntity, Integer> {
    List<AuditoriaConsultaEntity> findByConsultaEntityIdOrderByFechaModificacionDesc(Integer idConsulta);
}
