package com.josbar.medisistemas.services;

import com.josbar.medisistemas.domain.dtos.auditoria.AuditoriaConsultaResponseDTO;
import com.josbar.medisistemas.domain.dtos.auditoria.AuditoriaDocumentoResponseDTO;

import java.util.List;

public interface AuditoriaService {
    List<AuditoriaConsultaResponseDTO> obtenerAuditoriasPorConsulta(Integer idConsulta);
    List<AuditoriaDocumentoResponseDTO> obtenerAuditoriasPorDocumento(Integer idDocumento);
}
