package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RefugioAprobacionAdminRequest(
        @NotNull(message = "El estado de aprobación es obligatorio")
        Boolean aprobado,

        @Size(max = 500, message = "El motivo no puede superar los 500 caracteres")
        String motivo
)
{}
