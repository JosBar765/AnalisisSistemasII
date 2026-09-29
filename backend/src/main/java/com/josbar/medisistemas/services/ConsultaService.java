package com.josbar.medisistemas.services;

import com.josbar.medisistemas.domain.dtos.consulta.ModificarConsultaRequestDTO;
import com.josbar.medisistemas.domain.entities.ConsultaEntity;

public interface ConsultaService {
    ConsultaEntity registrar(ConsultaEntity entity);
    ConsultaEntity findById(Integer id);
    ConsultaEntity modificar(Integer id, ModificarConsultaRequestDTO request);
}
