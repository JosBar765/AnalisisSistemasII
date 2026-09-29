package com.josbar.medisistemas.domain.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * El JWT contiene en su payload el id del usuario (sub), su rol y su nombre;
 * Angular lo decodifica para saber quién inició sesión.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponseDTO {

    private String token;
}
