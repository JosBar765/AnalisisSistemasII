package com.josbar.medisistemas.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "\"SignosVitales\"")
public class SignosVitalesEntity {

    @Id
    @Column(name = "id_consulta")
    private Integer idConsulta;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_consulta")
    private ConsultaEntity consultaEntity;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal peso;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal altura;

    @Column(name = "presion_sistolica", nullable = false)
    private Integer presionSistolica;

    @Column(name = "presion_diastolica", nullable = false)
    private Integer presionDiastolica;

    @Column(precision = 3, scale = 1, nullable = false)
    private BigDecimal temperatura;

    // Getters y setters
}