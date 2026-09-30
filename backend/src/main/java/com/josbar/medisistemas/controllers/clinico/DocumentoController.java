package com.josbar.medisistemas.controllers.clinico;

import com.josbar.medisistemas.domain.dtos.documento.ActualizarDocumentoRequestDTO;
import com.josbar.medisistemas.domain.dtos.documento.DocumentoResponseDTO;
import com.josbar.medisistemas.domain.dtos.documento.EnlaceDocumentoResponseDTO;
import com.josbar.medisistemas.domain.dtos.documento.SubirDocumentoRequestDTO;
import com.josbar.medisistemas.domain.entities.DocumentoEntity;
import com.josbar.medisistemas.mappers.impl.DocumentoMapper;
import com.josbar.medisistemas.services.DocumentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoResponseDTO> subirDocumento(
            @RequestPart("archivo") MultipartFile archivo,
            @Valid @ModelAttribute SubirDocumentoRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        DocumentoEntity saved = documentoService.subir(
                documentoMapper.toEntity(request), archivo, Integer.valueOf(jwt.getSubject()));
        return new ResponseEntity<>(documentoMapper.toResponse(saved), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoResponseDTO> actualizarDocumento(
            @PathVariable("id") Integer id,
            @RequestPart("archivo") MultipartFile archivo,
            @Valid @ModelAttribute ActualizarDocumentoRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        DocumentoEntity updated = documentoService.actualizar(id, request, archivo, Integer.valueOf(jwt.getSubject()));
        return new ResponseEntity<>(documentoMapper.toResponse(updated), HttpStatus.OK);
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<DocumentoResponseDTO>> listarDocumentosPorPaciente(@PathVariable("idPaciente") Integer idPaciente) {
        List<DocumentoResponseDTO> documentos = documentoService.listarPorPaciente(idPaciente).stream()
                .map(documentoMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(documentos, HttpStatus.OK);
    }

    @GetMapping("/{id}/enlace")
    public ResponseEntity<EnlaceDocumentoResponseDTO> obtenerEnlaceLectura(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(new EnlaceDocumentoResponseDTO(documentoService.obtenerEnlaceLectura(id)), HttpStatus.OK);
    }
}
