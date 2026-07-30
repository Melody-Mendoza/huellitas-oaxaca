package com.huellitasoaxaca.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huellitasoaxaca.backend.dto.response.RefugioDonacionResponse;
import com.huellitasoaxaca.backend.services.RefugioDonacionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/refugios")
@RequiredArgsConstructor
public class RefugioDonacionController
{
    private final RefugioDonacionService refugioDonacionService;

    @GetMapping("/disponibles-para-donacion")
    public ResponseEntity<List<RefugioDonacionResponse>> listarDisponibles()
    {
        return ResponseEntity.ok(
                refugioDonacionService.listarDisponibles()
        );
    }
}
