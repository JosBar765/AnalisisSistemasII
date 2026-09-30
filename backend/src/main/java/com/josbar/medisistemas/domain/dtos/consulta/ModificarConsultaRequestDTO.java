package com.josbar.medisistemas.domain.dtos.consulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Los campos no enviados (null) no se modifican. Los textos enviados no pueden quedar vacíos. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModificarConsultaRequestDTO {
    private static final String NO_VACIO = ".*\\S.*";

    @NotNull(message = "El motivo de modificación es obligatorio.")
    private Integer idMotivoModificacionConsulta;
    @Pattern(regexp = NO_VACIO, message = "El motivo de consulta no puede estar vacío.")
    private String motivoConsulta;
    @Pattern(regexp = NO_VACIO, message = "El diagnóstico no puede estar vacío.")
    private String diagnostico;
    @Pattern(regexp = NO_VACIO, message = "El tratamiento no puede estar vacío.")
    private String tratamiento;
    private String observaciones;
    @Valid
    private SignosVitalesRequestDTO signosVitalesRequestDTO;
}
