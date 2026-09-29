package com.josbar.medisistemas.controllers.paciente;

import com.josbar.medisistemas.domain.dtos.paciente.PacienteRequestDTO;
import com.josbar.medisistemas.domain.dtos.paciente.PacienteResponseDTO;
import com.josbar.medisistemas.domain.entities.PacienteEntity;
import com.josbar.medisistemas.mappers.impl.PacienteMapper;
import com.josbar.medisistemas.services.PacienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;
    private final PacienteMapper pacienteMapper;

    public PacienteController(PacienteService pacienteService, PacienteMapper pacienteMapper) {
        this.pacienteService = pacienteService;
        this.pacienteMapper = pacienteMapper;
    }

    @PostMapping
    public ResponseEntity<PacienteResponseDTO> registrarPaciente(@RequestBody PacienteRequestDTO request) {
        PacienteEntity saved = pacienteService.save(pacienteMapper.toEntity(request));
        return new ResponseEntity<>(pacienteMapper.toResponse(saved), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> consultarPacientes() {
        List<PacienteResponseDTO> pacientes = pacienteService.findAll().stream()
                .map(pacienteMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(pacientes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> consultarPacientePorId(@PathVariable("id") Integer id) {
        PacienteEntity entity = pacienteService.findById(id);
        if (entity == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(pacienteMapper.toResponse(entity), HttpStatus.OK);
    }

    @GetMapping("/buscar")
    public ResponseEntity<PacienteResponseDTO> buscarPacientePorDpi(@RequestParam String dpi) {
        PacienteEntity entity = pacienteService.findByDpi(dpi);
        if (entity == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(pacienteMapper.toResponse(entity), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> modificarPaciente(
            @PathVariable("id") Integer id,
            @RequestBody PacienteRequestDTO request) {
        PacienteEntity updated = pacienteService.modificar(id, request);
        return new ResponseEntity<>(pacienteMapper.toResponse(updated), HttpStatus.OK);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PacienteResponseDTO> cambiarEstadoPaciente(
            @PathVariable("id") Integer id,
            @RequestParam Boolean estado) {
        PacienteEntity updatedEntity = pacienteService.cambiarEstado(id, estado);
        return new ResponseEntity<>(pacienteMapper.toResponse(updatedEntity), HttpStatus.OK);
    }
}