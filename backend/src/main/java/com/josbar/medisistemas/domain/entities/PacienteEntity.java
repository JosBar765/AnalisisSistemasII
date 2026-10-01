package com.josbar.medisistemas.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import com.josbar.medisistemas.domain.normalizacion.NormalizadorTextos;
import com.josbar.medisistemas.domain.normalizacion.Minusculas;
import jakarta.persistence.EntityListeners;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(NormalizadorTextos.class)
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
    @Minusculas
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 100)
    @Minusculas
    private String segundoNombre;

    @Column(name = "primer_apellido", length = 50, nullable = false)
    @Minusculas
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 50, nullable = false)
    @Minusculas
    private String segundoApellido;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(length = 15, nullable = false)
    private String telefono;

    @Column(length = 255, nullable = false)
    @Minusculas
    private String correo;

    @Column(length = 250, nullable = false)
    @Minusculas
    private String direccion;
}
