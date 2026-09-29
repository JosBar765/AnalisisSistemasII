package com.josbar.medisistemas.repositories;

import com.josbar.medisistemas.domain.dtos.dashboard.ConsultaPorMedicoDTO;
import com.josbar.medisistemas.domain.entities.ConsultaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConsultaRepository extends CrudRepository<ConsultaEntity, Integer> {
    List<ConsultaEntity> findByCitaEntityPacienteEntityId(Integer idPaciente);

    @Query("""
            SELECT new com.josbar.medisistemas.domain.dtos.dashboard.ConsultaPorMedicoDTO(
                m.id, CONCAT(u.primerNombre, ' ', u.primerApellido), COUNT(c))
            FROM ConsultaEntity c
            JOIN c.citaEntity ci
            JOIN ci.medicoEntity m
            JOIN m.usuarioEntity u
            GROUP BY m.id, u.primerNombre, u.primerApellido
            """)
    List<ConsultaPorMedicoDTO> obtenerConsultasPorMedico();
}
