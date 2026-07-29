package com.huellitasoaxaca.backend.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.huellitasoaxaca.backend.dto.request.SolicitudAdopcionCrearRequest;
import com.huellitasoaxaca.backend.dto.response.SolicitudAdopcionResponse;
import com.huellitasoaxaca.backend.services.SolicitudAdopcionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/solicitudes")
@PreAuthorize("hasRole('USUARIO')")
@RequiredArgsConstructor
public class SolicitudAdopcionController
{
    private final SolicitudAdopcionService solicitudService;

    @PostMapping
    public ResponseEntity<SolicitudAdopcionResponse> crear(
            @Valid @RequestBody SolicitudAdopcionCrearRequest request,
            Authentication authentication
    )
    {
        SolicitudAdopcionResponse response = solicitudService.crear(
                request,
                authentication.getName()
        );
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
