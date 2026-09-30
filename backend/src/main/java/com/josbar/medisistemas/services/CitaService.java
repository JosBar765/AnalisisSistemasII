package com.josbar.medisistemas.services;

import com.josbar.medisistemas.domain.dtos.cita.HorarioDisponibleResponseDTO;
import com.josbar.medisistemas.domain.entities.CitaEntity;

import java.time.LocalDate;
import java.util.List;

public interface CitaService {
    CitaEntity programar(CitaEntity entity);
    List<HorarioDisponibleResponseDTO> obtenerHorariosDisponibles(Integer idMedico, LocalDate fecha);
    List<CitaEntity> obtenerAgendaDiaria(LocalDate fecha);
    List<CitaEntity> obtenerAgendaPorRango(LocalDate desde, LocalDate hasta);
    List<CitaEntity> obtenerAgendaPorMedico(Integer idMedico, LocalDate fecha);
    /** Citas del propio médico en el rango (UC-MED-001). */
    List<CitaEntity> obtenerAgendaDelMedico(Integer idMedico, LocalDate desde, LocalDate hasta);

    /** Una cita, solo si pertenece al médico indicado. */
    CitaEntity obtenerCitaDelMedico(Integer id, Integer idMedico);

    CitaEntity cancelar(Integer id);
    CitaEntity reprogramar(Integer id, CitaEntity nuevaInformacion);
    CitaEntity registrarLlegada(Integer id);
    CitaEntity solicitarLlamado(Integer id, Integer idMedico);
}
