package com.josbar.medisistemas.controllers.admin;

import com.josbar.medisistemas.domain.dtos.usuario.UsuarioRequestDTO;
import com.josbar.medisistemas.domain.dtos.usuario.UsuarioResponseDTO;
import com.josbar.medisistemas.domain.entities.UsuarioEntity;
import com.josbar.medisistemas.mappers.impl.UsuarioMapper;
import com.josbar.medisistemas.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(@Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioEntity entity = usuarioMapper.toEntity(request);
        UsuarioEntity savedEntity = usuarioService.save(entity);
        return new ResponseEntity<>(usuarioMapper.toResponse(savedEntity), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.findAll().stream()
                .map(usuarioMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> consultarUsuario(@PathVariable("id") Integer id) {
        UsuarioEntity entity = usuarioService.findById(id);
        if (entity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(usuarioMapper.toResponse(entity), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> modificarUsuario(
            @PathVariable("id") Integer id,
            @Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioEntity updatedEntity = usuarioService.modificar(id, request);
        return new ResponseEntity<>(usuarioMapper.toResponse(updatedEntity), HttpStatus.OK);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponseDTO> cambiarEstadoUsuario(
            @PathVariable("id") Integer id,
            @RequestParam Boolean estado) {
        UsuarioEntity updatedEntity = usuarioService.cambiarEstado(id, estado);
        return new ResponseEntity<>(usuarioMapper.toResponse(updatedEntity), HttpStatus.OK);
    }

    @PatchMapping("/{id}/contrasenia")
    public ResponseEntity<Void> cambiarContrasenia(
            @PathVariable("id") Integer id,
            @RequestParam String nuevaContrasenia) {
        usuarioService.cambiarContrasenia(id, nuevaContrasenia);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}