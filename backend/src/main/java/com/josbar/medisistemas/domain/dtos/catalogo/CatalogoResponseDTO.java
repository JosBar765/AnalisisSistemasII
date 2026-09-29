package com.josbar.medisistemas.domain.dtos.catalogo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CatalogoResponseDTO {
    // Aplica para Rol, Especialidad, DiaSemana, EstadoCita, CategoriaDocumento
    private Integer id;
    private String nombre;
}