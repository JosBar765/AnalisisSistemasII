package com.josbar.medisistemas.domain.dtos.catalogo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CatalogoRequestDTO {
    private String nombre;
}
