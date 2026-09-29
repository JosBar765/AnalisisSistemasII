package com.josbar.medisistemas.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "\"AuditoriaDocumento\"")
public class AuditoriaDocumentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_documento", nullable = false)
    private DocumentoEntity documentoEntity;

    @Column(name = "nombre_anterior", length = 255, nullable = false)
    private String nombreAnterior;

    @Column(name = "url_anterior", length = 500)
    private String urlAnterior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioEntity usuarioEntity;

    @Column(name = "fecha_modificacion", nullable = false)
    private LocalDateTime fechaModificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_motivo_modificacion", nullable = false)
    private MotivoModificacionDocumentoEntity motivoModificacionDocumentoEntity;

    @Column(name = "nombre_nuevo", length = 255, nullable = false)
    private String nombreNuevo;

    @Column(name = "url_nuevo", length = 500)
    private String urlNuevo;
}