package com.josbar.medisistemas.domain.dtos.medico;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicoRequestDTO {
    // En tu DB, el id de Médico es el mismo de Usuario
    private Integer idUsuario;
    private Integer idEspecialidad;
    private String colegiado;
}
