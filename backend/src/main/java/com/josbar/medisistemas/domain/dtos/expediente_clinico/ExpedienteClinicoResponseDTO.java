package com.josbar.medisistemas.domain.dtos.expediente_clinico;

import com.josbar.medisistemas.domain.dtos.consulta.ConsultaResponseDTO;
import com.josbar.medisistemas.domain.dtos.documento.DocumentoResponseDTO;
import com.josbar.medisistemas.domain.dtos.paciente.PacienteResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpedienteClinicoResponseDTO {
    private PacienteResponseDTO pacienteResponseDTO;

    private List<ConsultaResponseDTO> historialConsultasResponseDTO;

    private List<DocumentoResponseDTO> documentosClinicosResponseDTO;
}
