package com.huellitasoaxaca.backend.dto.response;

import java.time.LocalDateTime;

import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;

public record SolicitudAdminResumenResponse(
        Long id,
        LocalDateTime fechaSolicitud,
        EstadoSolicitud estado,
        Long usuarioId,
        String nombreUsuario,
        Long mascotaId,
        String nombreMascota
)
{}
