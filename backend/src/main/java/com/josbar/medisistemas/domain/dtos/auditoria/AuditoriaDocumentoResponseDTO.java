package com.josbar.medisistemas.domain.dtos.auditoria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditoriaDocumentoResponseDTO {
    private Integer id;

    private Integer idDocumento;

    private String nombreAnterior;
    private String urlAnterior;

    private Integer idUsuario;

    private LocalDateTime fechaModificacion;

    private Integer idMotivoModificacionDocumento;

    private String nombreNuevo;
    private String urlNuevo;
}
