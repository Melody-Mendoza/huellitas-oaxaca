package com.huellitasoaxaca.backend.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huellitasoaxaca.backend.dto.request.LoginRequest;
import com.huellitasoaxaca.backend.dto.request.UsuarioRegistroRequest;
import com.huellitasoaxaca.backend.dto.response.AuthResponse;
import com.huellitasoaxaca.backend.dto.response.UsuarioResponse;
import com.huellitasoaxaca.backend.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController 
{
        private final AuthService authService;

        @PostMapping("/registro")
        public ResponseEntity<UsuarioResponse> registrar(
                @Valid @RequestBody UsuarioRegistroRequest request
        ) 
        {
                UsuarioResponse response = authService.registrar(request);

                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(response);
        }

        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(
                @Valid @RequestBody LoginRequest request
        ) 
        {
                return ResponseEntity.ok(authService.login(request));
        }

        @GetMapping("/me")
        public ResponseEntity<UsuarioResponse> obtenerPerfil(
                Authentication authentication
        ) 
        {
                UsuarioResponse response = authService.obtenerUsuarioAutenticado(authentication.getName());

                return ResponseEntity.ok(response);
        }

        @PostMapping("/logout")
        public ResponseEntity<Map<String, String>> logout(
                Authentication authentication
        ) 
        {
                Jwt jwt = (Jwt) authentication.getPrincipal();

                authService.logout(
                        jwt.getId(),
                        jwt.getSubject(),
                        jwt.getExpiresAt()
                );

                return ResponseEntity.ok(
                        Map.of(
                                "mensaje",
                                "Sesión cerrada correctamente"
                        )
                );
        }
}
