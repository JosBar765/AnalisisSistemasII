package com.josbar.medisistemas.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "\"Cita\"")
public class CitaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medico", nullable = false)
    private MedicoEntity medicoEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente", nullable = false)
    private PacienteEntity pacienteEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_cita", nullable = false)
    private EstadoCitaEntity estadoCitaEntity;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(name = "hora_llegada")
    private LocalTime horaLlegada;

    @Column(name = "hora_solicitud_llamado")
    private LocalTime horaSolicitudLlamado;

    /** Cuántas veces se canceló esta cita. No baja al reprogramarla: es el historial que usa el dashboard. */
    @Builder.Default
    @Column(name = "veces_cancelada", nullable = false)
    private Integer vecesCancelada = 0;
}
