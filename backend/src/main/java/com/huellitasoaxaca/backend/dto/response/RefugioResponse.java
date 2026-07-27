package com.huellitasoaxaca.backend.dto.response;

public record RefugioResponse(
    Long id,
    String nombre,
    String descripcion,
    String direccion,
    String telefono,
    String correo,
    String sitioWeb,
    Boolean activo,
    Long usuarioId,
    String nombreResponsable

) 
{}
