package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ImagenMascotaActualizarRequest(
        @NotBlank(message = "La URL de la imagen es obligatoria")
        @Size(
                max = 500,
                message = "La URL de la imagen no puede superar los 500 caracteres"
        )
        String url) 
{}
