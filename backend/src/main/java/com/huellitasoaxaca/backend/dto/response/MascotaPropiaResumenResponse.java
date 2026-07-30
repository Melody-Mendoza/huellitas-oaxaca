package com.huellitasoaxaca.backend.dto.response;

import java.time.LocalDate;

import com.huellitasoaxaca.backend.entity.enums.Especie;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;
import com.huellitasoaxaca.backend.entity.enums.SexoMascota;
import com.huellitasoaxaca.backend.entity.enums.TamanoMascota;

public record MascotaPropiaResumenResponse(
        Long id,
        String nombre,
        Especie especie,
        String raza,
        SexoMascota sexo,
        Integer edad,
        TamanoMascota tamano,
        EstadoMascota estado,
        LocalDate fechaIngreso,
        String imagenPrincipal
) {}
