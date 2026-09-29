package com.josbar.medisistemas.domain.dtos.paciente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PacienteResponseDTO {
    private Integer id;

    private String dpi;

    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;

    private LocalDate fechaNacimiento;

    private String telefono;
    private String correo;
    private String direccion;

    private Boolean estado;
}
