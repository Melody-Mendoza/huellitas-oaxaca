package com.huellitasoaxaca.backend.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huellitasoaxaca.backend.dto.request.CambiarPasswordRequest;
import com.huellitasoaxaca.backend.dto.request.UsuarioActualizarRequest;
import com.huellitasoaxaca.backend.dto.response.UsuarioResponse;
import com.huellitasoaxaca.backend.services.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/perfil")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class PerfilController
{
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<UsuarioResponse> obtenerPerfil(
            Authentication authentication
    )
    {
        return ResponseEntity.ok(
                usuarioService.obtenerActivoPorCorreo(authentication.getName())
        );
    }

    @PutMapping
    public ResponseEntity<UsuarioResponse> actualizarPerfil(
            Authentication authentication,
            @Valid @RequestBody UsuarioActualizarRequest request
    )
    {
        return ResponseEntity.ok(
                usuarioService.actualizarPerfil(
                        authentication.getName(),
                        request
                )
        );
    }

    @PatchMapping("/password")
    public ResponseEntity<Map<String, String>> cambiarPassword(
            Authentication authentication,
            @Valid @RequestBody CambiarPasswordRequest request
    )
    {
        usuarioService.cambiarPassword(authentication.getName(), request);

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "La contraseña se actualizó correctamente"
                )
        );
    }
}
