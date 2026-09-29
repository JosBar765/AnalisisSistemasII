package com.josbar.medisistemas.domain.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultaPorMedicoDTO {
    private Integer idMedico;
    private String nombreMedico;
    private Long cantidad;
}
