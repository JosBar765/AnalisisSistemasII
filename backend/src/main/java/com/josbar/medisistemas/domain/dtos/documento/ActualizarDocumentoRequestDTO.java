package com.josbar.medisistemas.domain.dtos.documento;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos del formulario multipart para reemplazar el archivo de un documento.
 * El nuevo archivo viaja como parte "archivo"; la categoría solo se envía si cambia.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActualizarDocumentoRequestDTO {
    @NotNull
    private Integer idMotivoModificacion;

    private Integer idCategoriaDocumento;
}
