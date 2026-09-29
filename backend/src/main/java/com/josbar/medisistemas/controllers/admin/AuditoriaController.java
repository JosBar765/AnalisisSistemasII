package com.josbar.medisistemas.controllers.admin;

import com.josbar.medisistemas.domain.dtos.auditoria.AuditoriaConsultaResponseDTO;
import com.josbar.medisistemas.domain.dtos.auditoria.AuditoriaDocumentoResponseDTO;
import com.josbar.medisistemas.services.AuditoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auditorias")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/consultas/{idConsulta}")
    public ResponseEntity<List<AuditoriaConsultaResponseDTO>> listarAuditoriasConsulta(
            @PathVariable("idConsulta") Integer idConsulta) {
        return new ResponseEntity<>(auditoriaService.obtenerAuditoriasPorConsulta(idConsulta), HttpStatus.OK);
    }

    @GetMapping("/documentos/{idDocumento}")
    public ResponseEntity<List<AuditoriaDocumentoResponseDTO>> listarAuditoriasDocumento(
            @PathVariable("idDocumento") Integer idDocumento) {
        return new ResponseEntity<>(auditoriaService.obtenerAuditoriasPorDocumento(idDocumento), HttpStatus.OK);
    }
}