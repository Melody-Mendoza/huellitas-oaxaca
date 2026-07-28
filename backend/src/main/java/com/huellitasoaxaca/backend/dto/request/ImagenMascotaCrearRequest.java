package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ImagenMascotaCrearRequest(
        @NotBlank(message = "La URL de la imagen es obligatoria")
        @Size(
                max = 500,
                message = "La URL de la imagen no puede superar los 500 caracteres"
        )
        String url,

        @NotNull(message = "La mascota es obligatoria")
        @Positive(message = "El identificador de la mascota debe ser válido")
        Long mascotaId
) 
{}
