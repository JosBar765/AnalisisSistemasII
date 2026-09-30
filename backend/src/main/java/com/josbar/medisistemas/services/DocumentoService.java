package com.josbar.medisistemas.services;

import com.josbar.medisistemas.domain.dtos.documento.ActualizarDocumentoRequestDTO;
import com.josbar.medisistemas.domain.entities.DocumentoEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentoService {
    DocumentoEntity subir(DocumentoEntity entity, MultipartFile archivo, Integer idUsuarioCarga);
    DocumentoEntity actualizar(Integer id, ActualizarDocumentoRequestDTO request, MultipartFile archivo, Integer idUsuario);
    List<DocumentoEntity> listarPorPaciente(Integer idPaciente);
    String obtenerEnlaceLectura(Integer id);
}
