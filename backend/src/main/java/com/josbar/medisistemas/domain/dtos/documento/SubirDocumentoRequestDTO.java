package com.josbar.medisistemas.domain.dtos.documento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubirDocumentoRequestDTO {
    private Integer idPaciente;
    private Integer idCategoriaDocumento;

    private String nombre;
    private String url;

    // Temporal: mientras no exista JWT, el usuario que carga el documento
    // se recibe explícitamente en el request en vez de tomarse del contexto de seguridad.
    private Integer idUsuarioCarga;
}
