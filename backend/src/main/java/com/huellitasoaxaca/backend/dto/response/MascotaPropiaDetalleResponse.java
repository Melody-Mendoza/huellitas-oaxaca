package com.huellitasoaxaca.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.huellitasoaxaca.backend.entity.enums.Especie;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;
import com.huellitasoaxaca.backend.entity.enums.SexoMascota;
import com.huellitasoaxaca.backend.entity.enums.TamanoMascota;

public record MascotaPropiaDetalleResponse(
        Long id,
        String nombre,
        Especie especie,
        String raza,
        SexoMascota sexo,
        Integer edad,
        BigDecimal peso,
        TamanoMascota tamano,
        String descripcion,
        EstadoMascota estado,
        LocalDate fechaIngreso,
        String imagenPrincipal,
        List<String> imagenesAdicionales,
        Long refugioId,
        String nombreRefugio
) {}
