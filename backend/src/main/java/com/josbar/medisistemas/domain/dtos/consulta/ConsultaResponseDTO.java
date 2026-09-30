package com.josbar.medisistemas.domain.dtos.consulta;

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
public class ConsultaResponseDTO {
    private Integer id;
    private Integer idCita;
    private LocalDate fecha;
    private LocalTime hora;
    private Integer idMedico;
    private String nombreMedico;
    private Integer idPaciente;
    private String nombrePaciente;
    private String motivoConsulta;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private SignosVitalesRequestDTO signosVitalesRequestDTO;
}
