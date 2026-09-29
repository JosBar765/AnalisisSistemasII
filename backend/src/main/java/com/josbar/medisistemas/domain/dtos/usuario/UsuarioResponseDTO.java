package com.josbar.medisistemas.domain.dtos.usuario;

import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioResponseDTO {
    private Integer id;

    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;

    private String correo;
    private String telefono;

    private Boolean estado;
    private LocalDateTime fechaCreacion;

    private CatalogoResponseDTO rol;
}