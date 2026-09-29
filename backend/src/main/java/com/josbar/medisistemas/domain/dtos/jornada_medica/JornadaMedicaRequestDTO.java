package com.josbar.medisistemas.domain.dtos.jornada_medica;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JornadaMedicaRequestDTO {
    private Integer idMedico;
    private Integer idDiaSemana;

    private LocalTime horaInicio;
    private LocalTime horaFin;

    private Integer duracionConsulta;
}
