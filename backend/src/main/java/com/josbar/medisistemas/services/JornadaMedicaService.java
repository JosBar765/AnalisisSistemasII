package com.josbar.medisistemas.services;

import com.josbar.medisistemas.domain.dtos.jornada_medica.JornadaMedicaRequestDTO;
import com.josbar.medisistemas.domain.entities.JornadaMedicaEntity;

import java.util.List;

public interface JornadaMedicaService {
    JornadaMedicaEntity save(JornadaMedicaEntity entity);
    List<JornadaMedicaEntity> findByMedicoId(Integer idMedico);
    JornadaMedicaEntity modificar(Integer id, JornadaMedicaRequestDTO request);
    void deleteById(Integer id);
}
