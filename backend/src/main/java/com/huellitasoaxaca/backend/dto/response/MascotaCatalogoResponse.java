package com.huellitasoaxaca.backend.dto.response;

import com.huellitasoaxaca.backend.entity.enums.Especie;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;
import com.huellitasoaxaca.backend.entity.enums.SexoMascota;
import com.huellitasoaxaca.backend.entity.enums.TamanoMascota;

public record MascotaCatalogoResponse(
        Long id,
        String nombre,
        Especie especie,
        String raza,
        Integer edad,
        SexoMascota sexo,
        TamanoMascota tamano,
        EstadoMascota estado,
        String imagenPrincipal,
        RefugioCatalogoResponse refugio
) {}
