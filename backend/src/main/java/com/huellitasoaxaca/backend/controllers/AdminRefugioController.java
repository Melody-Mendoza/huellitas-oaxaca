package com.huellitasoaxaca.backend.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huellitasoaxaca.backend.dto.request.RefugioAdminCrearRequest;
import com.huellitasoaxaca.backend.dto.request.RefugioCompletoAdminCrearRequest;
import com.huellitasoaxaca.backend.dto.request.RefugioAprobacionAdminRequest;
import com.huellitasoaxaca.backend.dto.request.RefugioEstadoAdminRequest;
import com.huellitasoaxaca.backend.dto.request.RefugioResponsableAdminRequest;
import com.huellitasoaxaca.backend.dto.response.PaginaResponse;
import com.huellitasoaxaca.backend.dto.response.RefugioAdminDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.RefugioAdminResumenResponse;
import com.huellitasoaxaca.backend.services.AdminRefugioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/refugios")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminRefugioController
{
    private final AdminRefugioService adminRefugioService;

    @GetMapping
    public ResponseEntity<PaginaResponse<RefugioAdminResumenResponse>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String aprobado,
            @RequestParam(required = false) String activo,
            @RequestParam(required = false) Long responsableId,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(adminRefugioService.listar(
                page,
                size,
                sort,
                texto,
                aprobado,
                activo,
                responsableId,
                authentication.getName()
        ));
    }

    @PostMapping
    public ResponseEntity<RefugioAdminDetalleResponse> crear(
            @Valid @RequestBody RefugioAdminCrearRequest request,
            Authentication authentication
    )
    {
        RefugioAdminDetalleResponse creado = adminRefugioService.crear(
                request,
                authentication.getName()
        );
        return ResponseEntity
                .created(URI.create("/api/admin/refugios/" + creado.id()))
                .body(creado);
    }

    @PostMapping("/completo")
    public ResponseEntity<RefugioAdminDetalleResponse> crearCompleto(
            @Valid @RequestBody RefugioCompletoAdminCrearRequest request,
            Authentication authentication
    )
    {
        RefugioAdminDetalleResponse creado = adminRefugioService.crearCompleto(
                request,
                authentication.getName()
        );
        return ResponseEntity
                .created(URI.create("/api/admin/refugios/" + creado.id()))
                .body(creado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RefugioAdminDetalleResponse> obtener(
            @PathVariable Long id,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(adminRefugioService.obtener(
                id,
                authentication.getName()
        ));
    }

    @PatchMapping("/{id}/responsable")
    public ResponseEntity<RefugioAdminDetalleResponse> cambiarResponsable(
            @PathVariable Long id,
            @Valid @RequestBody RefugioResponsableAdminRequest request,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(adminRefugioService.cambiarResponsable(
                id,
                request.responsableId(),
                request.motivo(),
                authentication.getName()
        ));
    }

    @PatchMapping("/{id}/aprobacion")
    public ResponseEntity<RefugioAdminDetalleResponse> cambiarAprobacion(
            @PathVariable Long id,
            @Valid @RequestBody RefugioAprobacionAdminRequest request,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(adminRefugioService.cambiarAprobacion(
                id,
                request.aprobado(),
                request.motivo(),
                authentication.getName()
        ));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<RefugioAdminDetalleResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody RefugioEstadoAdminRequest request,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(adminRefugioService.cambiarEstado(
                id,
                request.activo(),
                request.motivo(),
                authentication.getName()
        ));
    }
}
