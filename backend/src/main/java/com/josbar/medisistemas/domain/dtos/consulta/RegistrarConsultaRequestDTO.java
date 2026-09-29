package com.josbar.medisistemas.domain.dtos.consulta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrarConsultaRequestDTO {
    private Integer idCita;

    private String motivoConsulta;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;

    private SignosVitalesRequestDTO signosVitalesRequestDTO;
}
