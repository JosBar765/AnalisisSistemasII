package com.josbar.medisistemas.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "\"MotivoModificacionDocumento\"")
public class MotivoModificacionDocumentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "motivo_modificacion", length = 50, nullable = false)
    private String motivoModificacion;

    @Column(name = "detalle_motivo")
    private String detalleMotivo;
}