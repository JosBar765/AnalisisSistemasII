package com.josbar.medisistemas.repositories;

import com.josbar.medisistemas.domain.entities.AuditoriaDocumentoEntity;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuditoriaDocumentoRepository extends CrudRepository<AuditoriaDocumentoEntity, Integer> {
    List<AuditoriaDocumentoEntity> findByDocumentoEntityId(Integer idDocumento);
    List<AuditoriaDocumentoEntity> findByDocumentoEntityPacienteEntityIdOrderByFechaModificacionDesc(Integer idPaciente);

    /** Rutas de archivos reemplazados: ya no son la versión activa, pero se conservan por auditoría. */
    @Query("SELECT a.urlAnterior FROM AuditoriaDocumentoEntity a WHERE a.urlAnterior IS NOT NULL")
    List<String> findAllUrlsAnteriores();

    @Query("SELECT a.urlNuevo FROM AuditoriaDocumentoEntity a WHERE a.urlNuevo IS NOT NULL")
    List<String> findAllUrlsNuevas();
}
