package com.josbar.medisistemas.domain.dtos.jornada_medica;

import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JornadaMedicaResponseDTO {

    private Integer id;

    private CatalogoResponseDTO diaSemanaResponseDTO;

    private LocalTime horaInicio;
    private LocalTime horaFin;

    private Integer duracionConsulta;
}