package com.huellitasoaxaca.backend.controllers;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huellitasoaxaca.backend.dto.request.DonacionCrearRequest;
import com.huellitasoaxaca.backend.dto.response.DonacionResponse;
import com.huellitasoaxaca.backend.services.DonacionService;
import com.huellitasoaxaca.backend.services.DonacionService.ResultadoCreacion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Flujo académico sin procesamiento financiero. COMPLETADA significa que el
 * usuario confirmó la donación simulada, no que exista un pago verificado.
 */
@RestController
@RequestMapping("/api/donaciones")
@PreAuthorize("hasRole('USUARIO')")
@RequiredArgsConstructor
public class DonacionController
{
    private final DonacionService donacionService;

    @PostMapping
    public ResponseEntity<DonacionResponse> crear(
            @Valid @RequestBody DonacionCrearRequest request,
            @RequestHeader(
                    value = "Idempotency-Key",
                    required = false
            ) String claveIdempotencia,
            Authentication authentication
    )
    {
        ResultadoCreacion resultado = donacionService.crear(
                request,
                claveIdempotencia,
                authentication.getName()
        );

        if (!resultado.nueva())
        {
            return ResponseEntity.ok(resultado.donacion());
        }

        URI location = URI.create(
                "/api/donaciones/" + resultado.donacion().id()
        );
        return ResponseEntity.created(location).body(resultado.donacion());
    }

    @GetMapping("/mis-donaciones")
    public ResponseEntity<Page<DonacionResponse>> listarPropias(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(
                donacionService.listarPropias(
                        authentication.getName(),
                        page,
                        size
                )
        );
    }

    @GetMapping("/{donacionId}")
    public ResponseEntity<DonacionResponse> obtenerPropia(
            @PathVariable Long donacionId,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(
                donacionService.obtenerPropia(
                        donacionId,
                        authentication.getName()
                )
        );
    }

    @PostMapping("/{donacionId}/confirmar")
    public ResponseEntity<DonacionResponse> confirmar(
            @PathVariable Long donacionId,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(
                donacionService.confirmar(
                        donacionId,
                        authentication.getName()
                )
        );
    }

    @PostMapping("/{donacionId}/cancelar")
    public ResponseEntity<DonacionResponse> cancelar(
            @PathVariable Long donacionId,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(
                donacionService.cancelar(
                        donacionId,
                        authentication.getName()
                )
        );
    }
}
