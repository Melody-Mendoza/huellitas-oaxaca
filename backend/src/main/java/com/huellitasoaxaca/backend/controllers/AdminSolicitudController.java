package com.huellitasoaxaca.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huellitasoaxaca.backend.dto.response.PaginaResponse;
import com.huellitasoaxaca.backend.dto.response.SolicitudAdminDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.SolicitudAdminResumenResponse;
import com.huellitasoaxaca.backend.services.AdminSolicitudService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/solicitudes")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminSolicitudController
{
    private final AdminSolicitudService adminSolicitudService;

    @GetMapping
    public ResponseEntity<PaginaResponse<SolicitudAdminResumenResponse>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long mascotaId,
            @RequestParam(required = false) Long refugioId,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(adminSolicitudService.listar(
                page, size, sort, texto, estado, mascotaId, refugioId,
                authentication.getName()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudAdminDetalleResponse> obtener(
            @PathVariable Long id,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(adminSolicitudService.obtener(
                id, authentication.getName()
        ));
    }
}
