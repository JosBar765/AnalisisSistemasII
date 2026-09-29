package com.josbar.medisistemas.services;

import com.josbar.medisistemas.domain.dtos.paciente.PacienteRequestDTO;
import com.josbar.medisistemas.domain.entities.PacienteEntity;

import java.util.List;

public interface PacienteService {
    PacienteEntity save(PacienteEntity entity);
    List<PacienteEntity> findAll();
    PacienteEntity findById(Integer id);
    PacienteEntity findByDpi(String dpi);
    PacienteEntity modificar(Integer id, PacienteRequestDTO request);
    PacienteEntity cambiarEstado(Integer id, Boolean estado);
}
