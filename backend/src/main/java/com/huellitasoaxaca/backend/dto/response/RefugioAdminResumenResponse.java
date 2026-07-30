package com.huellitasoaxaca.backend.dto.response;

public record RefugioAdminResumenResponse(
        Long id,
        String nombre,
        String correo,
        Boolean aprobado,
        Boolean activo,
        ResponsableRefugioResumenResponse responsable
)
{}
