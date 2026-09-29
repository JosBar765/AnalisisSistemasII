package com.josbar.medisistemas.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "\"Paciente\"")
public class PacienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Boolean estado = true;

    @Column(length = 13, unique = true, nullable = false)
    private String dpi;

    @Column(name = "primer_nombre", length = 50, nullable = false)
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 100)
    private String segundoNombre;

    @Column(name = "primer_apellido", length = 50, nullable = false)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 50, nullable = false)
    private String segundoApellido;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(length = 15, nullable = false)
    private String telefono;

    @Column(length = 255, nullable = false)
    private String correo;

    @Column(length = 250, nullable = false)
    private String direccion;
}
