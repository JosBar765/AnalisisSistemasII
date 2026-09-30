package com.josbar.medisistemas.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "\"AuditoriaConsulta\"")
public class AuditoriaConsultaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_consulta", nullable = false)
    private ConsultaEntity consultaEntity;

    @Column(name = "motivo_consulta_anterior")
    private String motivoConsultaAnterior;

    @Column(name = "diagnostico_anterior")
    private String diagnosticoAnterior;

    @Column(name = "tratamiento_anterior")
    private String tratamientoAnterior;

    @Column(name = "observaciones_anterior")
    private String observacionesAnterior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioEntity usuarioEntity;

    @Column(name = "fecha_modificacion", nullable = false)
    private LocalDateTime fechaModificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_motivo_modificacion", nullable = false)
    private MotivoModificacionConsultaEntity motivoModificacionConsultaEntity;

    @Column(name = "motivo_consulta_nuevo")
    private String motivoConsultaNuevo;

    @Column(name = "diagnostico_nuevo")
    private String diagnosticoNuevo;

    @Column(name = "tratamiento_nuevo")
    private String tratamientoNuevo;

    @Column(name = "observaciones_nuevo")
    private String observacionesNuevo;

    @Column(name = "peso_anterior", precision = 5, scale = 2)
    private BigDecimal pesoAnterior;

    @Column(name = "altura_anterior", precision = 5, scale = 2)
    private BigDecimal alturaAnterior;

    @Column(name = "presion_sistolica_anterior")
    private Integer presionSistolicaAnterior;

    @Column(name = "presion_diastolica_anterior")
    private Integer presionDiastolicaAnterior;

    @Column(name = "temperatura_anterior", precision = 3, scale = 1)
    private BigDecimal temperaturaAnterior;

    @Column(name = "peso_nuevo", precision = 5, scale = 2)
    private BigDecimal pesoNuevo;

    @Column(name = "altura_nueva", precision = 5, scale = 2)
    private BigDecimal alturaNueva;

    @Column(name = "presion_sistolica_nueva")
    private Integer presionSistolicaNueva;

    @Column(name = "presion_diastolica_nueva")
    private Integer presionDiastolicaNueva;

    @Column(name = "temperatura_nueva", precision = 3, scale = 1)
    private BigDecimal temperaturaNueva;

    // Getters y setters
}