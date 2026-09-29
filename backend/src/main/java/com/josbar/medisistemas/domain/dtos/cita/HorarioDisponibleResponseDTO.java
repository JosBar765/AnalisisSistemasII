package com.josbar.medisistemas.domain.dtos.cita;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HorarioDisponibleResponseDTO {
    private LocalTime hora;
}
