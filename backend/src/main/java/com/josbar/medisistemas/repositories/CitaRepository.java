package com.josbar.medisistemas.repositories;

import com.josbar.medisistemas.domain.entities.CitaEntity;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface CitaRepository extends CrudRepository<CitaEntity, Integer> {
    List<CitaEntity> findByFecha(LocalDate fecha);
    List<CitaEntity> findByMedicoEntityIdAndFecha(Integer idMedico, LocalDate fecha);
    List<CitaEntity> findByMedicoEntityIdAndFechaAndEstadoCitaEntityEstadoCitaNot(Integer idMedico, LocalDate fecha, String estadoCita);
    long countByEstadoCitaEntityEstadoCita(String estadoCita);
}
