package com.josbar.medisistemas.domain.dtos.consulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrarConsultaRequestDTO {
    @NotNull(message = "La cita es obligatoria.")
    private Integer idCita;
    @NotBlank(message = "El motivo de consulta es obligatorio.")
    private String motivoConsulta;
    @NotBlank(message = "El diagnóstico es obligatorio.")
    private String diagnostico;
    @NotBlank(message = "El tratamiento es obligatorio.")
    private String tratamiento;
    private String observaciones;
    @NotNull(message = "Los signos vitales son obligatorios.")
    @Valid
    private SignosVitalesRequestDTO signosVitalesRequestDTO;
}
