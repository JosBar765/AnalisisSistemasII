package com.josbar.medisistemas.services;

import com.josbar.medisistemas.domain.dtos.cita.HorarioDisponibleResponseDTO;
import com.josbar.medisistemas.domain.entities.CitaEntity;

import java.time.LocalDate;
import java.util.List;

public interface CitaService {
    CitaEntity programar(CitaEntity entity);
    List<HorarioDisponibleResponseDTO> obtenerHorariosDisponibles(Integer idMedico, LocalDate fecha);
    List<CitaEntity> obtenerAgendaDiaria(LocalDate fecha);
    List<CitaEntity> obtenerAgendaPorMedico(Integer idMedico, LocalDate fecha);
    CitaEntity cancelar(Integer id);
    CitaEntity reprogramar(Integer id, CitaEntity nuevaInformacion);
}
