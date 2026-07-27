package com.huellitasoaxaca.backend.dto.response;

import java.time.LocalDateTime;

import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;

public record SolicitudAdopcionResponse(
    Long id,
    LocalDateTime fechaSolicitud,
    EstadoSolicitud estado,
    String comentarios,
    Long usuarioId,
    String nombreUsuario,
    Long mascotaId,
    String nombreMascota
) 
{}
