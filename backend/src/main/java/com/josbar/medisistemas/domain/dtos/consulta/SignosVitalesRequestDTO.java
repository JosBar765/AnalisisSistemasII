package com.josbar.medisistemas.domain.dtos.consulta;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Límites amplios: solo descartan valores imposibles y respetan la precisión de las columnas. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignosVitalesRequestDTO {
    @NotNull(message = "El peso es obligatorio.")
    @DecimalMin(value = "0.1", message = "El peso debe ser mayor que 0.")
    @DecimalMax(value = "500.00", message = "El peso no es válido.")
    private BigDecimal peso; // en KG

    @NotNull(message = "La altura es obligatoria.")
    @DecimalMin(value = "20.0", message = "La altura debe expresarse en centímetros.")
    @DecimalMax(value = "260.00", message = "La altura no es válida.")
    private BigDecimal altura; // en CM

    @NotNull(message = "La presión sistólica es obligatoria.")
    @Min(value = 30, message = "La presión sistólica no es válida.")
    @Max(value = 300, message = "La presión sistólica no es válida.")
    private Integer presionSistolica;

    @NotNull(message = "La presión diastólica es obligatoria.")
    @Min(value = 20, message = "La presión diastólica no es válida.")
    @Max(value = 200, message = "La presión diastólica no es válida.")
    private Integer presionDiastolica;

    @NotNull(message = "La temperatura es obligatoria.")
    @DecimalMin(value = "25.0", message = "La temperatura no es válida.")
    @DecimalMax(value = "45.0", message = "La temperatura no es válida.")
    private BigDecimal temperatura;
}
