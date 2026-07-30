package com.huellitasoaxaca.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huellitasoaxaca.backend.dto.request.UsuarioEstadoAdminRequest;
import com.huellitasoaxaca.backend.dto.response.PaginaResponse;
import com.huellitasoaxaca.backend.dto.response.UsuarioResponse;
import com.huellitasoaxaca.backend.services.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUsuarioController
{
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<PaginaResponse<UsuarioResponse>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String activo,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(usuarioService.listarAdministrativamente(
                page,
                size,
                sort,
                texto,
                rol,
                activo,
                authentication.getName()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtener(
            @PathVariable Long id,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(usuarioService.obtenerAdministrativamente(
                id,
                authentication.getName()
        ));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioEstadoAdminRequest request,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(usuarioService.cambiarEstado(
                id,
                request.activo(),
                request.motivo(),
                authentication.getName()
        ));
    }
}
