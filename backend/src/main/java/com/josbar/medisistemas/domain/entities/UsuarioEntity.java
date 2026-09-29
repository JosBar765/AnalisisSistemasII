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
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 100)
    private String segundoNombre;

    @Column(name = "primer_apellido", length = 50, nullable = false)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 50)
    private String segundoApellido;

    @Column(length = 255, nullable = false, unique = true)
    private String correo;

    @Column(length = 15, nullable = false, unique = true)
    private String telefono;

    @Column(length = 255, nullable = false)
    private String contrasenia;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
}