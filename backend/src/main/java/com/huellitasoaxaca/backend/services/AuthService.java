package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.request.LoginRequest;
import com.huellitasoaxaca.backend.dto.request.UsuarioRegistroRequest;
import com.huellitasoaxaca.backend.dto.response.AuthResponse;
import com.huellitasoaxaca.backend.dto.response.UsuarioResponse;

public interface AuthService 
{
    UsuarioResponse registrar(UsuarioRegistroRequest request);

    AuthResponse login(LoginRequest request);

    UsuarioResponse obtenerUsuarioAutenticado(String correo);

    void logout(
        String jti,
        String correo,
        java.time.Instant fechaExpiracion
);
}
