package com.josbar.medisistemas.domain.dtos.cita;

import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoResponseDTO;
import com.josbar.medisistemas.domain.dtos.medico.MedicoResponseDTO;
import com.josbar.medisistemas.domain.dtos.paciente.PacienteResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CitaResponseDTO {
    private Integer id;

    private MedicoResponseDTO medicoResponseDTO;
    private PacienteResponseDTO pacienteResponseDTO;

    private CatalogoResponseDTO estadoCitaResponseDTO;

    private LocalDate fecha;
    private LocalTime hora;

    private LocalTime horaLlegada;
    private LocalTime horaSolicitudLlamado;
}
