package com.huellitasoaxaca.backend.dto.response;

public record FavoritoResponse(
    Long usuarioId,
    String nombreUsuario,
    Long mascotaId,
    String nombreMascota
) 
{}
