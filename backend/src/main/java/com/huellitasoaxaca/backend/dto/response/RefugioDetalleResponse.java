package com.huellitasoaxaca.backend.dto.response;

public record RefugioDetalleResponse(
        Long id,
        String nombre,
        String direccion,
        String telefono,
        String correo
) {}
