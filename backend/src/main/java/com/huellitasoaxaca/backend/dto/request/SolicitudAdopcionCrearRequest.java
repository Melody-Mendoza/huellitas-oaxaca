package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SolicitudAdopcionCrearRequest(
        @NotNull(message = "La mascota es obligatoria")
        @Positive(message = "El identificador de la mascota debe ser válido")
        Long mascotaId,

        @Size(
                max = 1000,
                message = "Los comentarios no pueden superar los 1000 caracteres"
        )
        String comentarios
) 
{}
