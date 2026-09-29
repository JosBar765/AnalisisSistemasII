package com.josbar.medisistemas.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "\"JornadaMedica\"")
public class JornadaMedicaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medico", nullable = false)
    private MedicoEntity medicoEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dia_semana", nullable = false)
    private DiaSemanaEntity diaSemanaEntity;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "duracion_consulta", nullable = false)
    private Integer duracionConsulta;
}
