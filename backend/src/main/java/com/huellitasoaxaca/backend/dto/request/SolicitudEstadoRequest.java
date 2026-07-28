package com.huellitasoaxaca.backend.dto.request;

import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SolicitudEstadoRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoSolicitud estado,

        @Size(
                max = 1000,
                message = "Las observaciones no pueden superar los 1000 caracteres"
        )
        String observaciones
) 
{}
