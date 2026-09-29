package com.josbar.medisistemas.controllers.catalogo;

import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoResponseDTO;
import com.josbar.medisistemas.services.CatalogoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalogos")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/roles")
    public ResponseEntity<List<CatalogoResponseDTO>> listarRoles() {
        return new ResponseEntity<>(catalogoService.listarRoles(), HttpStatus.OK);
    }

    @GetMapping("/dias-semana")
    public ResponseEntity<List<CatalogoResponseDTO>> listarDiasSemana() {
        return new ResponseEntity<>(catalogoService.listarDiasSemana(), HttpStatus.OK);
    }

    @GetMapping("/estados-cita")
    public ResponseEntity<List<CatalogoResponseDTO>> listarEstadosCita() {
        return new ResponseEntity<>(catalogoService.listarEstadosCita(), HttpStatus.OK);
    }

    @GetMapping("/categorias-documento")
    public ResponseEntity<List<CatalogoResponseDTO>> listarCategoriasDocumento() {
        return new ResponseEntity<>(catalogoService.listarCategoriasDocumento(), HttpStatus.OK);
    }

    @GetMapping("/especialidades")
    public ResponseEntity<List<CatalogoResponseDTO>> listarEspecialidades() {
        return new ResponseEntity<>(catalogoService.listarEspecialidades(), HttpStatus.OK);
    }

    @GetMapping("/motivos-consulta")
    public ResponseEntity<List<CatalogoResponseDTO>> listarMotivosModificacionConsulta() {
        return new ResponseEntity<>(catalogoService.listarMotivosConsulta(), HttpStatus.OK);
    }

    @GetMapping("/motivos-documento")
    public ResponseEntity<List<CatalogoResponseDTO>> listarMotivosModificacionDocumento() {
        return new ResponseEntity<>(catalogoService.listarMotivosDocumento(), HttpStatus.OK);
    }
}