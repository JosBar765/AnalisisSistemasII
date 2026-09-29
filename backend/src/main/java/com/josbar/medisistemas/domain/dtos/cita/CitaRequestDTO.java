package com.josbar.medisistemas.domain.dtos.cita;

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
public class CitaRequestDTO {
    private Integer idMedico;
    private Integer idPaciente;

    private LocalDate fecha;
    private LocalTime hora;
}