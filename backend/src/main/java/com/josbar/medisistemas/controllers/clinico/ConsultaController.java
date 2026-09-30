package com.josbar.medisistemas.controllers.clinico;

import com.josbar.medisistemas.domain.dtos.consulta.ConsultaResponseDTO;
import com.josbar.medisistemas.domain.dtos.consulta.ModificarConsultaRequestDTO;
import com.josbar.medisistemas.domain.dtos.consulta.RegistrarConsultaRequestDTO;
import com.josbar.medisistemas.domain.entities.ConsultaEntity;
import com.josbar.medisistemas.mappers.impl.ConsultaMapper;
import com.josbar.medisistemas.services.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;
    private final ConsultaMapper consultaMapper;

    public ConsultaController(ConsultaService consultaService, ConsultaMapper consultaMapper) {
        this.consultaService = consultaService;
        this.consultaMapper = consultaMapper;
    }

    @PostMapping
    public ResponseEntity<ConsultaResponseDTO> registrarConsulta(
            @Valid @RequestBody RegistrarConsultaRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        ConsultaEntity saved = consultaService.registrar(consultaMapper.toEntity(request), Integer.valueOf(jwt.getSubject()));
        return new ResponseEntity<>(consultaMapper.toResponse(saved), HttpStatus.CREATED);
    }

    @GetMapping("/mias")
    public ResponseEntity<List<ConsultaResponseDTO>> listarMisConsultas(
            @RequestParam LocalDate fecha,
            @AuthenticationPrincipal Jwt jwt) {
        List<ConsultaResponseDTO> consultas = consultaService.listarDelMedico(Integer.valueOf(jwt.getSubject()), fecha).stream()
                .map(consultaMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(consultas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> consultarPorId(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(consultaMapper.toResponse(consultaService.findById(id)), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> modificarConsulta(
            @PathVariable("id") Integer id,
            @Valid @RequestBody ModificarConsultaRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        ConsultaEntity updated = consultaService.modificar(id, request, Integer.valueOf(jwt.getSubject()));
        return new ResponseEntity<>(consultaMapper.toResponse(updated), HttpStatus.OK);
    }
}
