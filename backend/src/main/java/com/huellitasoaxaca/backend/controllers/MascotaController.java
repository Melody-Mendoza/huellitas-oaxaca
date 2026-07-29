package com.huellitasoaxaca.backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huellitasoaxaca.backend.dto.response.MascotaCatalogoResponse;
import com.huellitasoaxaca.backend.dto.response.MascotaDetalleResponse;
import com.huellitasoaxaca.backend.entity.enums.Especie;
import com.huellitasoaxaca.backend.entity.enums.SexoMascota;
import com.huellitasoaxaca.backend.entity.enums.TamanoMascota;
import com.huellitasoaxaca.backend.services.MascotaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
public class MascotaController
{
    private final MascotaService mascotaService;

    @GetMapping("/{id}")
    public ResponseEntity<MascotaDetalleResponse> obtenerDetalle(
            @PathVariable Long id
    )
    {
        return ResponseEntity.ok(mascotaService.obtenerDetallePublico(id));
    }

    @GetMapping
    public ResponseEntity<Page<MascotaCatalogoResponse>> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Especie especie,
            @RequestParam(required = false) SexoMascota sexo,
            @RequestParam(required = false) TamanoMascota tamano,
            @RequestParam(required = false) Integer edad,
            @RequestParam(required = false) Long refugioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nombre,asc") String sort
    )
    {
        return ResponseEntity.ok(mascotaService.listarCatalogo(
                nombre,
                especie,
                sexo,
                tamano,
                edad,
                refugioId,
                page,
                size,
                sort
        ));
    }
}
