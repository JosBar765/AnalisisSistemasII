package com.josbar.medisistemas.controllers.clinico;

import com.josbar.medisistemas.domain.dtos.consulta.ConsultaResponseDTO;
import com.josbar.medisistemas.domain.dtos.consulta.ModificarConsultaRequestDTO;
import com.josbar.medisistemas.domain.dtos.consulta.RegistrarConsultaRequestDTO;
import com.josbar.medisistemas.domain.entities.ConsultaEntity;
import com.josbar.medisistemas.mappers.impl.ConsultaMapper;
import com.josbar.medisistemas.services.ConsultaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ConsultaResponseDTO> registrarConsulta(@RequestBody RegistrarConsultaRequestDTO request) {
        ConsultaEntity saved = consultaService.registrar(consultaMapper.toEntity(request));
        return new ResponseEntity<>(consultaMapper.toResponse(saved), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> consultarPorId(@PathVariable("id") Integer id) {
        ConsultaEntity entity = consultaService.findById(id);
        if (entity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(consultaMapper.toResponse(entity), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> modificarConsulta(
            @PathVariable("id") Integer id,
            @RequestBody ModificarConsultaRequestDTO request) {
        ConsultaEntity updated = consultaService.modificar(id, request);
        return new ResponseEntity<>(consultaMapper.toResponse(updated), HttpStatus.OK);
    }
}