package com.huellitasoaxaca.backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.huellitasoaxaca.backend.dto.request.SolicitudAdopcionCrearRequest;
import com.huellitasoaxaca.backend.dto.response.HistorialSolicitudPropiaResponse;
import com.huellitasoaxaca.backend.dto.response.SolicitudAdopcionResponse;
import com.huellitasoaxaca.backend.dto.response.SolicitudPropiaDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.SolicitudPropiaResumenResponse;
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

    @GetMapping("/mis-solicitudes")
    public ResponseEntity<Page<SolicitudPropiaResumenResponse>> listarPropias(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(
                solicitudService.listarPropias(
                        authentication.getName(),
                        page,
                        size
                )
        );
    }

    @GetMapping("/mis-solicitudes/{solicitudId}")
    public ResponseEntity<SolicitudPropiaDetalleResponse> obtenerPropia(
            @PathVariable Long solicitudId,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(
                solicitudService.obtenerPropia(
                        solicitudId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/mis-solicitudes/{solicitudId}/historial")
    public ResponseEntity<List<HistorialSolicitudPropiaResponse>>
    listarHistorialPropio(
            @PathVariable Long solicitudId,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(
                solicitudService.listarHistorialPropio(
                        solicitudId,
                        authentication.getName()
                )
        );
    }
}
