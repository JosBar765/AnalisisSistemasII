package com.josbar.medisistemas.services;

import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoResponseDTO;

import java.util.List;

public interface CatalogoService {
    List<CatalogoResponseDTO> listarRoles();
    List<CatalogoResponseDTO> listarDiasSemana();
    List<CatalogoResponseDTO> listarEstadosCita();
    List<CatalogoResponseDTO> listarCategoriasDocumento();
    List<CatalogoResponseDTO> listarEspecialidades();
    List<CatalogoResponseDTO> listarMotivosConsulta();
    List<CatalogoResponseDTO> listarMotivosDocumento();
}
