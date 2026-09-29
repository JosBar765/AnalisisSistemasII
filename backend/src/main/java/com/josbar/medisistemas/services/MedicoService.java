package com.josbar.medisistemas.services;

import com.josbar.medisistemas.domain.dtos.medico.MedicoRequestDTO;
import com.josbar.medisistemas.domain.entities.MedicoEntity;

import java.util.List;

public interface MedicoService {
    MedicoEntity save(MedicoEntity entity);
    List<MedicoEntity> findAll();
    MedicoEntity findById(Integer id);
    MedicoEntity modificar(Integer id, MedicoRequestDTO request);
}
