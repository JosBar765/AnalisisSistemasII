package com.josbar.medisistemas.controllers.admin;

import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoRequestDTO;
import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoResponseDTO;
import com.josbar.medisistemas.services.EspecialidadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/especialidades")
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    public EspecialidadController(EspecialidadService especialidadService) {
        this.especialidadService = especialidadService;
    }

    @PostMapping
    public ResponseEntity<CatalogoResponseDTO> registrarEspecialidad(@Valid @RequestBody CatalogoRequestDTO request) {
        CatalogoResponseDTO response = especialidadService.crear(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogoResponseDTO> editarEspecialidad(
            @PathVariable("id") Integer id,
            @Valid @RequestBody CatalogoRequestDTO request) {
        CatalogoResponseDTO response = especialidadService.editar(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEspecialidad(@PathVariable("id") Integer id) {
        especialidadService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}