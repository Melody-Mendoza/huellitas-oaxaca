package com.huellitasoaxaca.backend.dto.response;

public record AuthResponse(
    String token,
    String tipo,
    Long expiresIn,
    UsuarioResponse usuario
) 
{}
