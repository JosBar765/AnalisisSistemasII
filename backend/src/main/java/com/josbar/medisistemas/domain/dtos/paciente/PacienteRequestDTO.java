package com.josbar.medisistemas.domain.dtos.paciente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PacienteRequestDTO {
    @NotBlank
    @Size(max = 13)
    private String dpi;

    @NotBlank
    @Size(max = 50)
    private String primerNombre;
    @Size(max = 100)
    private String segundoNombre;
    @NotBlank
    @Size(max = 50)
    private String primerApellido;
    @NotBlank
    @Size(max = 50)
    private String segundoApellido;

    @NotNull
    @Past
    private LocalDate fechaNacimiento;

    @NotBlank
    @Size(max = 15)
    private String telefono;
    @NotBlank
    @Email
    @Size(max = 255)
    private String correo;
    @NotBlank
    @Size(max = 250)
    private String direccion;

    private Boolean estado;
}