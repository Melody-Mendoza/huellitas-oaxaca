package com.huellitasoaxaca.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huellitasoaxaca.backend.dto.request.RefugioPerfilActualizarRequest;
import com.huellitasoaxaca.backend.dto.response.RefugioPanelResponse;
import com.huellitasoaxaca.backend.dto.response.RefugioPerfilResponse;
import com.huellitasoaxaca.backend.services.RefugioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/refugios")
@PreAuthorize("hasRole('REFUGIO')")
@RequiredArgsConstructor
public class RefugioController
{
    private final RefugioService refugioService;

    @GetMapping("/mis-refugios")
    public ResponseEntity<List<RefugioPerfilResponse>> listarPropios(
            Authentication authentication
    )
    {
        return ResponseEntity.ok(
                refugioService.listarPerfilesPropios(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{refugioId}/perfil")
    public ResponseEntity<RefugioPerfilResponse> obtenerPerfil(
            @PathVariable Long refugioId,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(
                refugioService.obtenerPerfilPropio(
                        refugioId,
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/{refugioId}/perfil")
    public ResponseEntity<RefugioPerfilResponse> actualizarPerfil(
            @PathVariable Long refugioId,
            @Valid @RequestBody RefugioPerfilActualizarRequest request,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(
                refugioService.actualizarPerfilPropio(
                        refugioId,
                        request,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{refugioId}/panel")
    public ResponseEntity<RefugioPanelResponse> obtenerPanel(
            @PathVariable Long refugioId,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(
                refugioService.obtenerPanelPropio(
                        refugioId,
                        authentication.getName()
                )
        );
    }
}
