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
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MascotaActualizarRequest(
        @Pattern(
                regexp = ".*\\S.*",
                message = "El nombre de la mascota no puede estar vacío"
        )
        @Size(
                max = 100,
                message = "El nombre no puede superar los 100 caracteres"
        )
        String nombre,

        Especie especie,

        @Pattern(
                regexp = ".*\\S.*",
                message = "La raza no puede estar vacía"
        )
        @Size(
                max = 100,
                message = "La raza no puede superar los 100 caracteres"
        )
        String raza,

        SexoMascota sexo,

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

        TamanoMascota tamano,

        @Pattern(
                regexp = ".*\\S.*",
                message = "La descripción no puede estar vacía"
        )
        @Size(
                max = 2000,
                message = "La descripción no puede superar los 2000 caracteres"
        )
        String descripcion
) 
{}
