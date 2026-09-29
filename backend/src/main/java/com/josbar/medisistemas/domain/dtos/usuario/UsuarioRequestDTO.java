package com.josbar.medisistemas.domain.dtos.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioRequestDTO {
    private Integer idRol;

    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;

    private String correo;
    private String telefono;
    private String contrasenia;

    private Boolean estado; // true para activo, false para inactivo
}