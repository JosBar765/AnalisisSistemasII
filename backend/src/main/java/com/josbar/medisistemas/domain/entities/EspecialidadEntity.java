package com.josbar.medisistemas.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.josbar.medisistemas.domain.normalizacion.NormalizadorTextos;
import com.josbar.medisistemas.domain.normalizacion.Minusculas;
import jakarta.persistence.EntityListeners;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(NormalizadorTextos.class)
@Table(name = "\"Especialidad\"")
public class EspecialidadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    @Minusculas
    private String especialidad;
}
