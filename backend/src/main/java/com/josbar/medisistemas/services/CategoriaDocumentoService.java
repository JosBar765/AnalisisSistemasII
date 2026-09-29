package com.josbar.medisistemas.services;

import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoRequestDTO;
import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoResponseDTO;

public interface CategoriaDocumentoService {
    CatalogoResponseDTO crear(CatalogoRequestDTO request);
    CatalogoResponseDTO editar(Integer id, CatalogoRequestDTO request);
    void eliminar(Integer id);
}
