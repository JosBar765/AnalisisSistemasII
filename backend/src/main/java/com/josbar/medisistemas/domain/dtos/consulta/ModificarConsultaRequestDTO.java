package com.josbar.medisistemas.domain.dtos.consulta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModificarConsultaRequestDTO {
    private Integer idMotivoModificacionConsulta;

    private String motivoConsulta;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
}
