package com.josbar.medisistemas.controllers.admin;

import com.josbar.medisistemas.domain.dtos.jornada_medica.JornadaMedicaRequestDTO;
import com.josbar.medisistemas.domain.dtos.jornada_medica.JornadaMedicaResponseDTO;
import com.josbar.medisistemas.domain.entities.JornadaMedicaEntity;
import com.josbar.medisistemas.mappers.impl.JornadaMedicaMapper;
import com.josbar.medisistemas.services.JornadaMedicaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/jornadas")
public class JornadaMedicaController {

    private final JornadaMedicaService jornadaMedicaService;
    private final JornadaMedicaMapper jornadaMedicaMapper;

    public JornadaMedicaController(JornadaMedicaService jornadaMedicaService, JornadaMedicaMapper jornadaMedicaMapper) {
        this.jornadaMedicaService = jornadaMedicaService;
        this.jornadaMedicaMapper = jornadaMedicaMapper;
    }

    @PostMapping
    public ResponseEntity<JornadaMedicaResponseDTO> registrarJornada(@RequestBody JornadaMedicaRequestDTO request) {
        JornadaMedicaEntity entity = jornadaMedicaService.save(jornadaMedicaMapper.toEntity(request));
        return new ResponseEntity<>(jornadaMedicaMapper.toResponse(entity), HttpStatus.CREATED);
    }

    @GetMapping("/medico/{idMedico}")
    public ResponseEntity<List<JornadaMedicaResponseDTO>> listarJornadasPorMedico(@PathVariable("idMedico") Integer idMedico) {
        List<JornadaMedicaResponseDTO> jornadas = jornadaMedicaService.findByMedicoId(idMedico).stream()
                .map(jornadaMedicaMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(jornadas, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JornadaMedicaResponseDTO> modificarJornada(
            @PathVariable("id") Integer id,
            @RequestBody JornadaMedicaRequestDTO request) {
        JornadaMedicaEntity updated = jornadaMedicaService.modificar(id, request);
        return new ResponseEntity<>(jornadaMedicaMapper.toResponse(updated), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarJornada(@PathVariable("id") Integer id) {
        jornadaMedicaService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}