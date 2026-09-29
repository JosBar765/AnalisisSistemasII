package com.josbar.medisistemas.domain.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponseDTO {

    private String token;

    // Nota: El JWT ya contiene en su "payload" el idUsuario y posiblemente el Rol.
    // Por tus reglas de diseño, Angular deberá decodificar el token para saber quién inició sesión,
}