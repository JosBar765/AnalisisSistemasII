package com.josbar.medisistemas.domain.dtos.medico;

import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoResponseDTO;
import com.josbar.medisistemas.domain.dtos.usuario.UsuarioResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicoResponseDTO {
    private Integer id;

    private UsuarioResponseDTO usuarioResponseDTO;
    private CatalogoResponseDTO especialidadResponseDTO;

    private String colegiado;
}