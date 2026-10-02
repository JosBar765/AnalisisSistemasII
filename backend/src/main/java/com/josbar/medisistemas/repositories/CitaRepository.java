package com.josbar.medisistemas.repositories;

import com.josbar.medisistemas.domain.entities.CitaEntity;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CitaRepository extends CrudRepository<CitaEntity, Integer> {
    List<CitaEntity> findByFechaOrderByHora(LocalDate fecha);

    List<CitaEntity> findByPacienteEntityIdAndFechaAndEstadoCitaEntityEstadoCitaNot(Integer idPaciente, LocalDate fecha, String estadoCita);
    List<CitaEntity> findByFechaBetweenOrderByFechaAscHoraAsc(LocalDate desde, LocalDate hasta);
    List<CitaEntity> findByMedicoEntityIdAndFechaBetweenOrderByFechaAscHoraAsc(Integer idMedico, LocalDate desde, LocalDate hasta);

    List<CitaEntity> findByMedicoEntityIdAndFechaOrderByHora(Integer idMedico, LocalDate fecha);
    List<CitaEntity> findByMedicoEntityIdAndFechaAndEstadoCitaEntityEstadoCitaNot(Integer idMedico, LocalDate fecha, String estadoCita);
    long countByEstadoCitaEntityEstadoCita(String estadoCita);

    /** Total histórico de cancelaciones (una cita cancelada y reprogramada sigue contando). */
    @Query("SELECT COALESCE(SUM(c.vecesCancelada), 0) FROM CitaEntity c")
    long totalCancelaciones();

    List<CitaEntity> findByMedicoEntityIdAndFechaAndHoraSolicitudLlamadoIsNotNullAndEstadoCitaEntityEstadoCita(
            Integer idMedico, LocalDate fecha, String estadoCita);
}
