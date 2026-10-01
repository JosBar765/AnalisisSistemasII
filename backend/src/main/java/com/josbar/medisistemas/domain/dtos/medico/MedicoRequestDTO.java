package com.josbar.medisistemas.domain.dtos.medico;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Pattern(regexp = ".*\\S.*", message = "El número de colegiado no puede estar vacío.")
    @Size(max = 15, message = "El número de colegiado no puede superar 15 caracteres.")
    private String colegiado;
}
