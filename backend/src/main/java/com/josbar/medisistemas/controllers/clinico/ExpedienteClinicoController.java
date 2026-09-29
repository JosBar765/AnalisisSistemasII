package com.josbar.medisistemas.controllers.clinico;

import com.josbar.medisistemas.domain.dtos.expediente_clinico.ExpedienteClinicoResponseDTO;
import com.josbar.medisistemas.services.ExpedienteClinicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expedientes")
public class ExpedienteClinicoController {

    private final ExpedienteClinicoService expedienteService;

    public ExpedienteClinicoController(ExpedienteClinicoService expedienteService) {
        this.expedienteService = expedienteService;
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<ExpedienteClinicoResponseDTO> consultarExpediente(@PathVariable("idPaciente") Integer idPaciente) {
        ExpedienteClinicoResponseDTO expediente = expedienteService.obtenerExpediente(idPaciente);
        return new ResponseEntity<>(expediente, HttpStatus.OK);
    }
}