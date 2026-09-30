package com.josbar.medisistemas.services;

import com.josbar.medisistemas.domain.dtos.consulta.ModificarConsultaRequestDTO;
import com.josbar.medisistemas.domain.entities.ConsultaEntity;

import java.time.LocalDate;
import java.util.List;

public interface ConsultaService {

    /** Registra la consulta de una cita propia del médico y pasa la cita a "Atendido". */
    ConsultaEntity registrar(ConsultaEntity entity, Integer idMedico);

    ConsultaEntity findById(Integer id);

    /** Consultas registradas por el médico en la fecha indicada. */
    List<ConsultaEntity> listarDelMedico(Integer idMedico, LocalDate fecha);

    /** Corrige una consulta finalizada dejando registro de auditoría. Solo el médico que la atendió. */
    ConsultaEntity modificar(Integer id, ModificarConsultaRequestDTO request, Integer idMedico);
}
