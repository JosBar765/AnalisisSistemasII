package com.josbar.medisistemas.services;

import com.josbar.medisistemas.domain.dtos.documento.ActualizarDocumentoRequestDTO;
import com.josbar.medisistemas.domain.entities.DocumentoEntity;

import java.util.List;

public interface DocumentoService {
    DocumentoEntity subir(DocumentoEntity entity);
    DocumentoEntity actualizar(Integer id, ActualizarDocumentoRequestDTO request);
    List<DocumentoEntity> listarPorPaciente(Integer idPaciente);
}
