package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.request.LoginRequest;
import com.huellitasoaxaca.backend.dto.request.GoogleLoginRequest;
import com.huellitasoaxaca.backend.dto.request.UsuarioRegistroRequest;
import com.huellitasoaxaca.backend.dto.response.AuthResponse;
import com.huellitasoaxaca.backend.dto.response.UsuarioResponse;

public interface AuthService 
{
    UsuarioResponse registrar(UsuarioRegistroRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse loginGoogle(GoogleLoginRequest request);

    UsuarioResponse obtenerUsuarioAutenticado(String correo);

    void logout(
        String jti,
        String correo,
        java.time.Instant fechaExpiracion
    );

    void solicitarRecuperacionPassword(String correo);

    void restablecerPassword(
            String token,
            String nuevaPassword,
            String confirmarPassword
    );
}
