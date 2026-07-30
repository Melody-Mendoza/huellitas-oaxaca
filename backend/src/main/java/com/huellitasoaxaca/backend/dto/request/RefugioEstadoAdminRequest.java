package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RefugioEstadoAdminRequest(
        @NotNull(message = "El estado activo es obligatorio")
        Boolean activo,

        @Size(max = 500, message = "El motivo no puede superar los 500 caracteres")
        String motivo
)
{}
