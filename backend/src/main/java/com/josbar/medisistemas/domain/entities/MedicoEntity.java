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
@Table(name = "\"Medico\"")
public class MedicoEntity {

    @Id
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private UsuarioEntity usuarioEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_especialidad", nullable = false)
    private EspecialidadEntity especialidad;

    @Column(length = 15, nullable = false)
    private String colegiado;
}
