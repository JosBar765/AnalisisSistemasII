package com.josbar.medisistemas.repositories;

import com.josbar.medisistemas.domain.entities.AuditoriaDocumentoEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuditoriaDocumentoRepository extends CrudRepository<AuditoriaDocumentoEntity, Integer> {
    List<AuditoriaDocumentoEntity> findByDocumentoEntityId(Integer idDocumento);
    List<AuditoriaDocumentoEntity> findByDocumentoEntityPacienteEntityIdOrderByFechaModificacionDesc(Integer idPaciente);
}
