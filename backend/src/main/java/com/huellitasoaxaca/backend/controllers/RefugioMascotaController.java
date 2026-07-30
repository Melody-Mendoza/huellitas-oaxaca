package com.huellitasoaxaca.backend.controllers;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.huellitasoaxaca.backend.dto.request.MascotaCrearRequest;
import com.huellitasoaxaca.backend.dto.request.MascotaActualizarRequest;
import com.huellitasoaxaca.backend.dto.request.MascotaEstadoActualizarRequest;
import com.huellitasoaxaca.backend.dto.response.ImagenMascotaResponse;
import com.huellitasoaxaca.backend.dto.response.MascotaPropiaDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.MascotaPropiaResumenResponse;
import com.huellitasoaxaca.backend.entity.enums.Especie;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;
import com.huellitasoaxaca.backend.services.MascotaService;
import com.huellitasoaxaca.backend.services.ImagenMascotaService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/refugios/{refugioId}/mascotas")
@PreAuthorize("hasRole('REFUGIO')")
@RequiredArgsConstructor
public class RefugioMascotaController
{
    private final MascotaService mascotaService;
    private final ImagenMascotaService imagenMascotaService;

    @PostMapping
    public ResponseEntity<MascotaPropiaDetalleResponse> crear(
            @PathVariable Long refugioId,
            @Valid @RequestBody MascotaCrearRequest request,
            Authentication authentication
    )
    {
        MascotaPropiaDetalleResponse creada = mascotaService.crearPropia(
                refugioId,
                request,
                authentication.getName()
        );
        URI ubicacion = URI.create(
                "/api/refugios/" + refugioId
                        + "/mascotas/" + creada.id()
        );

        return ResponseEntity.created(ubicacion).body(creada);
    }

    @GetMapping
    public ResponseEntity<Page<MascotaPropiaResumenResponse>> listar(
            @PathVariable Long refugioId,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Especie especie,
            @RequestParam(required = false) EstadoMascota estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(mascotaService.listarPropias(
                refugioId,
                nombre,
                especie,
                estado,
                page,
                size,
                authentication.getName()
        ));
    }

    @GetMapping("/{mascotaId}")
    public ResponseEntity<MascotaPropiaDetalleResponse> obtenerDetalle(
            @PathVariable Long refugioId,
            @PathVariable Long mascotaId,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(mascotaService.obtenerDetallePropio(
                refugioId,
                mascotaId,
                authentication.getName()
        ));
    }

    @PatchMapping("/{mascotaId}")
    public ResponseEntity<MascotaPropiaDetalleResponse> actualizar(
            @PathVariable Long refugioId,
            @PathVariable Long mascotaId,
            @Valid @RequestBody MascotaActualizarRequest request,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(mascotaService.actualizarPropia(
                refugioId,
                mascotaId,
                request,
                authentication.getName()
        ));
    }

    @PatchMapping("/{mascotaId}/estado")
    public ResponseEntity<MascotaPropiaDetalleResponse> actualizarEstado(
            @PathVariable Long refugioId,
            @PathVariable Long mascotaId,
            @Valid @RequestBody MascotaEstadoActualizarRequest request,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(mascotaService.actualizarEstadoPropio(
                refugioId,
                mascotaId,
                request,
                authentication.getName()
        ));
    }

    @PostMapping(
            path = "/{mascotaId}/imagenes",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ImagenMascotaResponse> guardarImagen(
            @PathVariable Long refugioId,
            @PathVariable Long mascotaId,
            @RequestPart("imagen") MultipartFile imagen,
            Authentication authentication
    )
    {
        ImagenMascotaResponse guardada = imagenMascotaService.guardarPropia(
                refugioId,
                mascotaId,
                imagen,
                authentication.getName()
        );
        URI ubicacion = URI.create(
                "/api/refugios/" + refugioId
                        + "/mascotas/" + mascotaId
                        + "/imagenes/" + guardada.id()
        );

        return ResponseEntity.created(ubicacion).body(guardada);
    }

    @GetMapping("/{mascotaId}/imagenes")
    public ResponseEntity<List<ImagenMascotaResponse>> listarImagenes(
            @PathVariable Long refugioId,
            @PathVariable Long mascotaId,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(imagenMascotaService.listarPropias(
                refugioId,
                mascotaId,
                authentication.getName()
        ));
    }

    @PatchMapping("/{mascotaId}/imagenes/{imagenId}/principal")
    public ResponseEntity<ImagenMascotaResponse> establecerPrincipal(
            @PathVariable Long refugioId,
            @PathVariable Long mascotaId,
            @PathVariable Long imagenId,
            Authentication authentication
    )
    {
        return ResponseEntity.ok(
                imagenMascotaService.establecerPrincipalPropia(
                        refugioId,
                        mascotaId,
                        imagenId,
                        authentication.getName()
                )
        );
    }

    @DeleteMapping("/{mascotaId}/imagenes/{imagenId}")
    public ResponseEntity<Void> eliminarImagen(
            @PathVariable Long refugioId,
            @PathVariable Long mascotaId,
            @PathVariable Long imagenId,
            Authentication authentication
    )
    {
        imagenMascotaService.eliminarPropia(
                refugioId,
                mascotaId,
                imagenId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}
