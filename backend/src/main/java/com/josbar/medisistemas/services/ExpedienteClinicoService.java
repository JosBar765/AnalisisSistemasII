package com.josbar.medisistemas.services;

import com.josbar.medisistemas.domain.dtos.expediente_clinico.ExpedienteClinicoResponseDTO;

public interface ExpedienteClinicoService {
    ExpedienteClinicoResponseDTO obtenerExpediente(Integer idPaciente);
}
