package com.huellitasoaxaca.backend.dto.response;

public record ResponsableRefugioResumenResponse(
        Long id,
        String nombreCompleto,
        Boolean activo,
        String rol
)
{}
