package com.huellitasoaxaca.backend.dto.response;

public record ImagenMascotaResponse (
    Long id,
    String url,
    Long mascotaId,
    String nombreMascota
)
{}
