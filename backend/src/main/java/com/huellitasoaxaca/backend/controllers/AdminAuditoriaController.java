package com.huellitasoaxaca.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huellitasoaxaca.backend.dto.response.AuditoriaAdminDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.AuditoriaAdminResumenResponse;
import com.huellitasoaxaca.backend.dto.response.PaginaResponse;
import com.huellitasoaxaca.backend.services.AdminAuditoriaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/auditoria")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminAuditoriaController
{
    private final AdminAuditoriaService adminAuditoriaService;

    @GetMapping
    public ResponseEntity<PaginaResponse<AuditoriaAdminResumenResponse>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String tipoAccion,
            @RequestParam(required = false) String tipoRecurso,
            @RequestParam(required = false) Long administradorId,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(adminAuditoriaService.listar(
                page, size, sort, tipoAccion, tipoRecurso, administradorId,
                desde, hasta, authentication.getName()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditoriaAdminDetalleResponse> obtener(
            @PathVariable Long id,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(adminAuditoriaService.obtener(
                id, authentication.getName()
        ));
    }
}
