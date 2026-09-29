package com.josbar.medisistemas.repositories;

import com.josbar.medisistemas.domain.entities.DocumentoEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DocumentoRepository extends CrudRepository<DocumentoEntity, Integer> {
    List<DocumentoEntity> findByPacienteEntityId(Integer idPaciente);
}
