package com.huellitasoaxaca.backend.dto.response;

public record RefugioPerfilResponse(
        Long id,
        String nombre,
        String descripcion,
        String direccion,
        String telefono,
        String correo,
        Boolean activo
) {}
