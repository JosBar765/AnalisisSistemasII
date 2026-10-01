package com.josbar.medisistemas.controllers.admin;

import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoRequestDTO;
import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoResponseDTO;
import com.josbar.medisistemas.services.CategoriaDocumentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categorias-documento")
public class CategoriaDocumentoController {

    private final CategoriaDocumentoService categoriaDocumentoService;

    public CategoriaDocumentoController(CategoriaDocumentoService categoriaDocumentoService) {
        this.categoriaDocumentoService = categoriaDocumentoService;
    }

    @PostMapping
    public ResponseEntity<CatalogoResponseDTO> registrarCategoria(@Valid @RequestBody CatalogoRequestDTO request) {
        CatalogoResponseDTO response = categoriaDocumentoService.crear(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogoResponseDTO> editarCategoria(
            @PathVariable("id") Integer id,
            @Valid @RequestBody CatalogoRequestDTO request) {
        CatalogoResponseDTO response = categoriaDocumentoService.editar(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable("id") Integer id) {
        categoriaDocumentoService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}