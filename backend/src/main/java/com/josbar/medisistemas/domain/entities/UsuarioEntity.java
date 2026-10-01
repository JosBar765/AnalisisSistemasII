package com.josbar.medisistemas.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.josbar.medisistemas.domain.normalizacion.NormalizadorTextos;
import com.josbar.medisistemas.domain.normalizacion.Minusculas;
import jakarta.persistence.EntityListeners;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(NormalizadorTextos.class)
@Table(name = "\"Usuario\"")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private RolEntity rolEntity;

    @Column(nullable = false)
    private Boolean estado = true;

    @Column(name = "primer_nombre", length = 50, nullable = false)
    @Minusculas
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 100)
    @Minusculas
    private String segundoNombre;

    @Column(name = "primer_apellido", length = 50, nullable = false)
    @Minusculas
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 50)
    @Minusculas
    private String segundoApellido;

    @Column(length = 255, nullable = false, unique = true)
    @Minusculas
    private String correo;

    @Column(length = 15, nullable = false, unique = true)
    private String telefono;

    @Column(length = 255, nullable = false)
    private String contrasenia;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
}