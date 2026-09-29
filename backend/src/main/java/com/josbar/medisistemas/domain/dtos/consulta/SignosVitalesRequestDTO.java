package com.josbar.medisistemas.domain.dtos.consulta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignosVitalesRequestDTO {
    private BigDecimal peso; // en KG
    private BigDecimal altura; // en CM

    private Integer presionSistolica;
    private Integer presionDiastolica;

    private BigDecimal temperatura;
}
