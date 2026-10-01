package com.josbar.medisistemas.mappers.impl;

import com.josbar.medisistemas.domain.dtos.consulta.ConsultaResponseDTO;
import com.josbar.medisistemas.domain.dtos.consulta.ModificarConsultaRequestDTO;
import com.josbar.medisistemas.domain.dtos.consulta.RegistrarConsultaRequestDTO;
import com.josbar.medisistemas.domain.dtos.consulta.SignosVitalesRequestDTO;
import com.josbar.medisistemas.domain.entities.CitaEntity;
import com.josbar.medisistemas.domain.entities.ConsultaEntity;
import com.josbar.medisistemas.domain.entities.SignosVitalesEntity;
import com.josbar.medisistemas.mappers.Mapper;
import org.springframework.stereotype.Component;
import com.josbar.medisistemas.utils.Texto;

@Component
public class ConsultaMapper implements Mapper<ConsultaEntity, RegistrarConsultaRequestDTO, ConsultaResponseDTO> {

    @Override
    public ConsultaEntity toEntity(RegistrarConsultaRequestDTO request) {
        if(request == null) return null;
        ConsultaEntity entity = new ConsultaEntity();

        CitaEntity cita = new CitaEntity();
        cita.setId(request.getIdCita());
        entity.setCitaEntity(cita);

        entity.setMotivoConsulta(request.getMotivoConsulta());
        entity.setDiagnostico(request.getDiagnostico());
        entity.setTratamiento(request.getTratamiento());
        entity.setObservaciones(request.getObservaciones());

        if (request.getSignosVitalesRequestDTO() != null) {
            SignosVitalesEntity sv = new SignosVitalesEntity();
            sv.setPeso(request.getSignosVitalesRequestDTO().getPeso());
            sv.setAltura(request.getSignosVitalesRequestDTO().getAltura());
            sv.setPresionSistolica(request.getSignosVitalesRequestDTO().getPresionSistolica());
            sv.setPresionDiastolica(request.getSignosVitalesRequestDTO().getPresionDiastolica());
            sv.setTemperatura(request.getSignosVitalesRequestDTO().getTemperatura());

            // ¡LA SOLUCIÓN AL ERROR LÓGICO ESTÁ AQUÍ!
            sv.setConsultaEntity(entity); // 1. Los Signos Vitales conocen a su Consulta
            entity.setSignosVitalesEntity(sv); // 2. La Consulta conoce a sus Signos Vitales
        }
        return entity;
    }

    @Override
    public ConsultaResponseDTO toResponse(ConsultaEntity entity) {
        if(entity == null) return null;
        ConsultaResponseDTO dto = new ConsultaResponseDTO();
        dto.setId(entity.getId());
        if (entity.getCitaEntity() != null) {
            CitaEntity cita = entity.getCitaEntity();
            dto.setIdCita(cita.getId());
            dto.setFecha(cita.getFecha());
            dto.setHora(cita.getHora());
            dto.setIdMedico(cita.getMedicoEntity().getId());
            dto.setNombreMedico(Texto.titulo(cita.getMedicoEntity().getUsuarioEntity().getPrimerNombre() + " "
                    + cita.getMedicoEntity().getUsuarioEntity().getPrimerApellido()));
            dto.setIdPaciente(cita.getPacienteEntity().getId());
            dto.setNombrePaciente(Texto.titulo(cita.getPacienteEntity().getPrimerNombre() + " "
                    + cita.getPacienteEntity().getPrimerApellido()));
        }
        dto.setMotivoConsulta(entity.getMotivoConsulta());
        dto.setDiagnostico(entity.getDiagnostico());
        dto.setTratamiento(entity.getTratamiento());
        dto.setObservaciones(entity.getObservaciones());

        // Como ya hay bidireccionalidad, extraerlos para la respuesta es súper directo:
        if (entity.getSignosVitalesEntity() != null) {
            SignosVitalesRequestDTO svDto = new SignosVitalesRequestDTO();
            svDto.setPeso(entity.getSignosVitalesEntity().getPeso());
            svDto.setAltura(entity.getSignosVitalesEntity().getAltura());
            svDto.setPresionSistolica(entity.getSignosVitalesEntity().getPresionSistolica());
            svDto.setPresionDiastolica(entity.getSignosVitalesEntity().getPresionDiastolica());
            svDto.setTemperatura(entity.getSignosVitalesEntity().getTemperatura());
            dto.setSignosVitalesRequestDTO(svDto);
        }
        return dto;
    }

    /**
     * Actualiza una entidad Consulta existente con los datos proporcionados en el DTO.
     * Solo se actualizan los campos que no vengan nulos en la petición (incluidos los signos vitales).
     */
    public void updateEntity(ModificarConsultaRequestDTO request, ConsultaEntity entity) {
        if (request == null || entity == null) { return; }

        if (request.getMotivoConsulta() != null) {
            entity.setMotivoConsulta(request.getMotivoConsulta());
        }

        if (request.getDiagnostico() != null) {
            entity.setDiagnostico(request.getDiagnostico());
        }

        if (request.getTratamiento() != null) {
            entity.setTratamiento(request.getTratamiento());
        }

        if (request.getObservaciones() != null) {
            entity.setObservaciones(request.getObservaciones());
        }
        if (request.getSignosVitalesRequestDTO() != null) {
            SignosVitalesEntity sv = entity.getSignosVitalesEntity();
            if (sv == null) {
                sv = new SignosVitalesEntity();
                sv.setConsultaEntity(entity);
                entity.setSignosVitalesEntity(sv);
            }
            sv.setPeso(request.getSignosVitalesRequestDTO().getPeso());
            sv.setAltura(request.getSignosVitalesRequestDTO().getAltura());
            sv.setPresionSistolica(request.getSignosVitalesRequestDTO().getPresionSistolica());
            sv.setPresionDiastolica(request.getSignosVitalesRequestDTO().getPresionDiastolica());
            sv.setTemperatura(request.getSignosVitalesRequestDTO().getTemperatura());
        }
    }
}