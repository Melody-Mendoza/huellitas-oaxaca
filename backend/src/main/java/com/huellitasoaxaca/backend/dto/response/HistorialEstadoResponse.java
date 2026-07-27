package com.huellitasoaxaca.backend.dto.response;

import java.time.LocalDateTime;

import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;

public record HistorialEstadoResponse(
    Long id,
    Long solicitudId,
    EstadoSolicitud estado,
    LocalDateTime fecha,
    String observaciones
) 
{}
