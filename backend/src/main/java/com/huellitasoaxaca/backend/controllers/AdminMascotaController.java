package com.huellitasoaxaca.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huellitasoaxaca.backend.dto.response.MascotaAdminDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.MascotaAdminResumenResponse;
import com.huellitasoaxaca.backend.dto.response.PaginaResponse;
import com.huellitasoaxaca.backend.services.AdminMascotaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/mascotas")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminMascotaController
{
    private final AdminMascotaService adminMascotaService;

    @GetMapping
    public ResponseEntity<PaginaResponse<MascotaAdminResumenResponse>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) String sexo,
            @RequestParam(required = false) String tamano,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long refugioId,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(adminMascotaService.listar(
                page, size, sort, texto, especie, sexo, tamano, estado,
                refugioId, authentication.getName()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaAdminDetalleResponse> obtener(
            @PathVariable Long id,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(adminMascotaService.obtener(
                id, authentication.getName()
        ));
    }
}
