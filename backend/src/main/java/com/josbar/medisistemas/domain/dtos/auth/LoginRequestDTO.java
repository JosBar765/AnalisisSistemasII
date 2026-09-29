package com.josbar.medisistemas.domain.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequestDTO {

    @NotBlank(message = "El correo es obligatorio.")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria.")
    private String contrasenia;
}
