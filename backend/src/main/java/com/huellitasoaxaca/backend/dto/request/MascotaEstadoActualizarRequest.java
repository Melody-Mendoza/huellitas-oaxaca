package com.huellitasoaxaca.backend.dto.request;

import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;

import jakarta.validation.constraints.NotNull;

public record MascotaEstadoActualizarRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoMascota estado
) {}
