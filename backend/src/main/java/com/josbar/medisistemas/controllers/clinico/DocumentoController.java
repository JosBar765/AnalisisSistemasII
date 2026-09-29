package com.josbar.medisistemas.controllers.clinico;

import com.josbar.medisistemas.domain.dtos.documento.SubirDocumentoRequestDTO;
import com.josbar.medisistemas.domain.dtos.documento.ActualizarDocumentoRequestDTO;
import com.josbar.medisistemas.domain.dtos.documento.DocumentoResponseDTO;
import com.josbar.medisistemas.domain.entities.DocumentoEntity;
import com.josbar.medisistemas.mappers.impl.DocumentoMapper;
import com.josbar.medisistemas.services.DocumentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/documentos")
public class DocumentoController {

    private final DocumentoService documentoService;
    private final DocumentoMapper documentoMapper;

    public DocumentoController(DocumentoService documentoService, DocumentoMapper documentoMapper) {
        this.documentoService = documentoService;
        this.documentoMapper = documentoMapper;
    }

    @PostMapping
    public ResponseEntity<DocumentoResponseDTO> subirDocumento(@RequestBody SubirDocumentoRequestDTO request) {
        // En tu service se debe setear el ID del usuarioCarga desde el JWT
        DocumentoEntity saved = documentoService.subir(documentoMapper.toEntity(request));
        return new ResponseEntity<>(documentoMapper.toResponse(saved), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentoResponseDTO> actualizarDocumento(
            @PathVariable("id") Integer id,
            @RequestBody ActualizarDocumentoRequestDTO request) {
        DocumentoEntity updated = documentoService.actualizar(id, request);
        return new ResponseEntity<>(documentoMapper.toResponse(updated), HttpStatus.OK);
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<DocumentoResponseDTO>> listarDocumentosPorPaciente(@PathVariable("idPaciente") Integer idPaciente) {
        List<DocumentoResponseDTO> documentos = documentoService.listarPorPaciente(idPaciente).stream()
                .map(documentoMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(documentos, HttpStatus.OK);
    }
}