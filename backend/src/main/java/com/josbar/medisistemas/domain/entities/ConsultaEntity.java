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
@Table(name = "\"Consulta\"")
public class ConsultaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cita", nullable = false, unique = true)
    private CitaEntity citaEntity;

    @Column(name = "motivo_consulta")
    private String motivoConsulta;

    @Column
    private String diagnostico;

    @Column
    private String tratamiento;

    @Column
    private String observaciones;

    // Dentro de ConsultaEntity.java
    @OneToOne(mappedBy = "consultaEntity", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private SignosVitalesEntity signosVitalesEntity;

    // Getters y setters
}