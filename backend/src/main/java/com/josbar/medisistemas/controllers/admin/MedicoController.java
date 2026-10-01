package com.josbar.medisistemas.controllers.admin;

import com.josbar.medisistemas.domain.dtos.medico.MedicoRequestDTO;
import com.josbar.medisistemas.domain.dtos.medico.MedicoResponseDTO;
import com.josbar.medisistemas.domain.entities.MedicoEntity;
import com.josbar.medisistemas.mappers.impl.MedicoMapper;
import com.josbar.medisistemas.services.MedicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    private final MedicoService medicoService;
    private final MedicoMapper medicoMapper;

    public MedicoController(MedicoService medicoService, MedicoMapper medicoMapper) {
        this.medicoService = medicoService;
        this.medicoMapper = medicoMapper;
    }

    @PostMapping
    public ResponseEntity<MedicoResponseDTO> registrarMedico(@RequestBody MedicoRequestDTO request) {
        var entity = medicoService.save(medicoMapper.toEntity(request));
        return new ResponseEntity<>(medicoMapper.toResponse(entity), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MedicoResponseDTO>> listarMedicos() {
        List<MedicoResponseDTO> medicos = medicoService.findAll().stream()
                .map(medicoMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(medicos, HttpStatus.OK);
    }

    /** Perfil del médico autenticado (el id del médico es el mismo del usuario, o sea el `sub` del JWT). */
    @GetMapping("/me")
    public ResponseEntity<MedicoResponseDTO> consultarMiPerfil(@AuthenticationPrincipal Jwt jwt) {
        MedicoEntity entity = medicoService.findById(Integer.valueOf(jwt.getSubject()));
        return new ResponseEntity<>(medicoMapper.toResponse(entity), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> consultarMedico(@PathVariable("id") Integer id) {
        MedicoEntity entity = medicoService.findById(id);
        if (entity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(medicoMapper.toResponse(entity), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> modificarMedico(
            @PathVariable("id") Integer id,
            @RequestBody MedicoRequestDTO request) {
        MedicoEntity updated = medicoService.modificar(id, request);
        return new ResponseEntity<>(medicoMapper.toResponse(updated), HttpStatus.OK);
    }
}