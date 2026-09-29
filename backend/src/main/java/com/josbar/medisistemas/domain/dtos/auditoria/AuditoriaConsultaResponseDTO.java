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
public class AuditoriaConsultaResponseDTO {
    private Integer id;

    private Integer idConsulta;

    private String motivoConsultaAnterior;
    private String diagnosticoAnterior;
    private String tratamientoAnterior;
    private String observacionesAnterior;

    private Integer idUsuario;

    private LocalDateTime fechaModificacion;

    private Integer idMotivoModificacion;

    private String motivoConsultaNuevo;
    private String diagnosticoNuevo;
    private String tratamientoNuevo;
    private String observacionesNuevo;
}
