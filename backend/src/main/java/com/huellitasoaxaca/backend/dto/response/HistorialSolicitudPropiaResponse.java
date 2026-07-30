package com.huellitasoaxaca.backend.dto.response;

import java.time.LocalDateTime;

import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;

public record HistorialSolicitudPropiaResponse(
        EstadoSolicitud estado,
        LocalDateTime fecha
) {}
