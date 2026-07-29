package com.huellitasoaxaca.backend.controllers;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @PatchMapping(
            value = "/foto",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UsuarioResponse> actualizarFotoPerfil(
            @AuthenticationPrincipal Jwt jwt,
            @RequestPart("foto") MultipartFile foto
    )
    {
        return ResponseEntity.ok(
                usuarioService.actualizarFotoPerfil(
                        jwt.getSubject(),
                        foto
                )
        );
    }

    @DeleteMapping("/foto")
    public ResponseEntity<UsuarioResponse> eliminarFotoPerfil(
            @AuthenticationPrincipal Jwt jwt
    )
    {
        return ResponseEntity.ok(
                usuarioService.eliminarFotoPerfil(jwt.getSubject())
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
