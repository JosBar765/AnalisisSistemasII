package com.josbar.medisistemas.domain.dtos.cita;

import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private Integer idMedico;
    @NotNull
    private Integer idPaciente;

    @NotNull
    private LocalDate fecha;
    @NotNull
    private LocalTime hora;
}