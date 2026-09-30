package com.josbar.medisistemas.domain.dtos.documento;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos del formulario multipart para subir un documento. El archivo viaja como parte
 * "archivo"; el nombre, la URL y el usuario que carga los determina el Backend.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubirDocumentoRequestDTO {
    @NotNull
    private Integer idPaciente;
    @NotNull
    private Integer idCategoriaDocumento;
}
