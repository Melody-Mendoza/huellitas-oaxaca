package com.huellitasoaxaca.backend.dto.response;

import com.huellitasoaxaca.backend.entity.enums.Especie;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;
import com.huellitasoaxaca.backend.entity.enums.SexoMascota;
import com.huellitasoaxaca.backend.entity.enums.TamanoMascota;
import java.time.LocalDateTime;

public record FavoritoResponse(
    Long mascotaId,
    String nombre,
    Especie especie,
    String raza,
    SexoMascota sexo,
    Integer edad,
    TamanoMascota tamano,
    EstadoMascota estado,
    String imagenPrincipal,
    Long refugioId,
    String nombreRefugio,
    boolean disponibleParaAdopcion,
    LocalDateTime fechaAgregado
) 
{}
