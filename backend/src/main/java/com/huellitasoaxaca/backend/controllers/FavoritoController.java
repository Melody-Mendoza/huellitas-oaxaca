package com.huellitasoaxaca.backend.controllers;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huellitasoaxaca.backend.dto.response.FavoritoResponse;
import com.huellitasoaxaca.backend.services.FavoritoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/favoritos")
@PreAuthorize("hasRole('USUARIO')")
@RequiredArgsConstructor
public class FavoritoController
{
    private final FavoritoService favoritoService;

    @PostMapping("/{mascotaId}")
    public ResponseEntity<FavoritoResponse> agregar(
            @PathVariable Long mascotaId,
            Authentication authentication
    )
    {
        FavoritoResponse response = favoritoService.agregar(
                mascotaId,
                authentication.getName()
        );
        URI location = URI.create("/api/favoritos/" + mascotaId);

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<FavoritoResponse>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(
                favoritoService.listar(
                        authentication.getName(),
                        page,
                        size
                )
        );
    }

    @DeleteMapping("/{mascotaId}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long mascotaId,
            Authentication authentication
    )
    {
        favoritoService.eliminar(mascotaId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
