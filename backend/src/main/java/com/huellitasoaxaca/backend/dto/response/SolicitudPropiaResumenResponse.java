package com.huellitasoaxaca.backend.dto.response;

import java.time.LocalDateTime;

import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;

public record SolicitudPropiaResumenResponse(
        Long id,
        LocalDateTime fechaSolicitud,
        EstadoSolicitud estado,
        Long mascotaId,
        String nombreMascota,
        String imagenPrincipal,
        Long refugioId,
        String nombreRefugio
) {}
