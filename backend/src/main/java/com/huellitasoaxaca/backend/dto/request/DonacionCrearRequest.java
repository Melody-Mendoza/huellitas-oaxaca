package com.huellitasoaxaca.backend.dto.request;

import java.math.BigDecimal;

import com.huellitasoaxaca.backend.entity.enums.MetodoPago;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record DonacionCrearRequest(
        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(
                value = "1.00",
                message = "El monto mínimo de donación es 1.00"
        )
        BigDecimal monto,

        @NotNull(message = "El método de pago es obligatorio")
        MetodoPago metodoPago,

        @NotNull(message = "El refugio es obligatorio")
        @Positive(message = "El identificador del refugio debe ser válido")
        Long refugioId,

        @Size(
                max = 500,
                message = "El mensaje no puede superar los 500 caracteres"
        )
        String mensaje
) 
{}
