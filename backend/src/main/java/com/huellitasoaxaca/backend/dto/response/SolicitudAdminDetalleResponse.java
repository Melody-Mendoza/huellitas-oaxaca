package com.huellitasoaxaca.backend.dto.response;

import java.time.LocalDateTime;

import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;

public record SolicitudAdminDetalleResponse(
        Long id,
        LocalDateTime fechaSolicitud,
        EstadoSolicitud estado,
        String comentarios,
        Long usuarioId,
        String nombreUsuario,
        String correoUsuario,
        Long mascotaId,
        String nombreMascota,
        Long refugioId,
        String nombreRefugio
)
{}
