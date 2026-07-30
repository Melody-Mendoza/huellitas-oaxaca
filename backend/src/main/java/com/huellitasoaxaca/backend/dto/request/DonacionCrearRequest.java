package com.huellitasoaxaca.backend.dto.request;

import java.math.BigDecimal;

import com.huellitasoaxaca.backend.entity.enums.MetodoPago;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record DonacionCrearRequest(
        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(
                 value = "10.00",
                 message = "El monto mínimo de donación es 10.00"
         )
         @DecimalMax(
                 value = "50000.00",
                 message = "El monto máximo de donación es 50000.00"
         )
         @Digits(
                 integer = 5,
                 fraction = 2,
                 message = "El monto debe tener máximo dos decimales"
         )
         BigDecimal monto,

         @NotNull(message = "El refugio es obligatorio")
         @Positive(message = "El identificador del refugio debe ser válido")
         Long refugioId,

         @NotNull(message = "El método de pago es obligatorio")
         MetodoPago metodoPago,

        @Size(
                max = 500,
                message = "El mensaje no puede superar los 500 caracteres"
        )
        String mensaje
) 
{}
