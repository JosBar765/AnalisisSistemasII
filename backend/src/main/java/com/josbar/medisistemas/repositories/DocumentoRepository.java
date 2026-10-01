package com.josbar.medisistemas.repositories;

import com.josbar.medisistemas.domain.entities.DocumentoEntity;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocumentoRepository extends CrudRepository<DocumentoEntity, Integer> {
    List<DocumentoEntity> findByPacienteEntityId(Integer idPaciente);

    @Query("SELECT d.url FROM DocumentoEntity d")
    List<String> findAllUrls();
}
