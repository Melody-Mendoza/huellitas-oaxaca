package com.huellitasoaxaca.backend.dto.response;

import java.time.LocalDateTime;

import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;

public record SolicitudPropiaDetalleResponse(
        Long id,
        LocalDateTime fechaSolicitud,
        EstadoSolicitud estado,
        String comentarios,
        Long mascotaId,
        String nombreMascota,
        String imagenPrincipal,
        Long refugioId,
        String nombreRefugio
) {}
