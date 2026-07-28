package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SolicitudComentarioRequest(
        @NotBlank(message = "El comentario es obligatorio")
        @Size(
                max = 1000,
                message = "El comentario no puede superar los 1000 caracteres"
        )
        String comentario
) 
{}
