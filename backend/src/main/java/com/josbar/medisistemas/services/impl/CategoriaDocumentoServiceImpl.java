package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.CategoriaDocumentoService;
import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoRequestDTO;
import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoResponseDTO;
import com.josbar.medisistemas.domain.entities.CategoriaDocumentoEntity;
import com.josbar.medisistemas.exceptions.ResourceNotFoundException;
import com.josbar.medisistemas.mappers.impl.CatalogoMapper;
import com.josbar.medisistemas.repositories.CategoriaDocumentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaDocumentoServiceImpl implements CategoriaDocumentoService {

    private final CategoriaDocumentoRepository categoriaDocumentoRepository;
    private final CatalogoMapper catalogoMapper;

    public CategoriaDocumentoServiceImpl(CategoriaDocumentoRepository categoriaDocumentoRepository, CatalogoMapper catalogoMapper) {
        this.categoriaDocumentoRepository = categoriaDocumentoRepository;
        this.catalogoMapper = catalogoMapper;
    }

    @Override
    @Transactional
    public CatalogoResponseDTO crear(CatalogoRequestDTO request) {
        CategoriaDocumentoEntity entity = CategoriaDocumentoEntity.builder()
                .categoriaDocumento(request.getNombre())
                .build();
        return catalogoMapper.toResponse(categoriaDocumentoRepository.save(entity));
    }

    @Override
    @Transactional
    public CatalogoResponseDTO editar(Integer id, CatalogoRequestDTO request) {
        CategoriaDocumentoEntity entity = categoriaDocumentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría de documento con id " + id));
        entity.setCategoriaDocumento(request.getNombre());
        return catalogoMapper.toResponse(categoriaDocumentoRepository.save(entity));
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        if (!categoriaDocumentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró la categoría de documento con id " + id);
        }
        categoriaDocumentoRepository.deleteById(id);
    }
}
