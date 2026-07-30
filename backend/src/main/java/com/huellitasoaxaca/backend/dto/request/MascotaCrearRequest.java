package com.huellitasoaxaca.backend.dto.request;

import java.math.BigDecimal;

import com.huellitasoaxaca.backend.entity.enums.Especie;
import com.huellitasoaxaca.backend.entity.enums.SexoMascota;
import com.huellitasoaxaca.backend.entity.enums.TamanoMascota;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MascotaCrearRequest(
        @NotBlank(message = "El nombre de la mascota es obligatorio")
        @Size(
                max = 100,
                message = "El nombre no puede superar los 100 caracteres"
        )
        String nombre,

        @NotNull(message = "La especie es obligatoria")
        Especie especie,

        @NotBlank(message = "La raza es obligatoria")
        @Size(
                max = 100,
                message = "La raza no puede superar los 100 caracteres"
        )
        String raza,

        @NotNull(message = "El sexo es obligatorio")
        SexoMascota sexo,

        @NotNull(message = "La edad es obligatoria")
        @Min(
                value = 0,
                message = "La edad no puede ser negativa"
        )
        @Max(
                value = 40,
                message = "La edad no puede superar los 40 años"
        )
        Integer edad,

        @DecimalMin(
                value = "0.1",
                message = "El peso debe ser mayor que cero"
        )
        @DecimalMax(
                value = "999.99",
                message = "El peso no puede superar 999.99"
        )
        @Digits(
                integer = 3,
                fraction = 2,
                message = "El peso debe tener hasta 3 enteros y 2 decimales"
        )
        BigDecimal peso,

        @NotNull(message = "El tamaño es obligatorio")
        TamanoMascota tamano,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(
                max = 2000,
                message = "La descripción no puede superar los 2000 caracteres"
        )
        String descripcion
) 
{}
