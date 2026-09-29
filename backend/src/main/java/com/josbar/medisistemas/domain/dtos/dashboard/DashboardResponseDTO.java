package com.josbar.medisistemas.domain.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponseDTO {
    private Long pacientesRegistrados;
    private Long consultasRealizadas;
    private Long citasCanceladas;

    private List<ConsultaPorMedicoDTO> consultasPorMedico;
}
