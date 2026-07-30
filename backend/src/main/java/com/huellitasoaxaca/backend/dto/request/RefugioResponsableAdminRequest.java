package com.huellitasoaxaca.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RefugioResponsableAdminRequest(
        @NotNull(message = "El responsable es obligatorio")
        Long responsableId,

        @Size(max = 500, message = "El motivo no puede superar los 500 caracteres")
        String motivo
)
{}
