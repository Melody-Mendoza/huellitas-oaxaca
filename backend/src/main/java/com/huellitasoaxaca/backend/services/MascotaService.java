package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.response.MascotaResponse;
import com.huellitasoaxaca.backend.entity.enums.Especie;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;

import java.util.List;

public interface MascotaService 
{
    List<MascotaResponse> listarTodas();

    MascotaResponse obtenerPorId(Long id);

    List<MascotaResponse> buscarPorNombre(String nombre);

    List<MascotaResponse> listarPorRefugio(Long refugioId);

    List<MascotaResponse> listarPorEspecie(Especie especie);

    List<MascotaResponse> listarPorEstado(EstadoMascota estado);

    List<MascotaResponse> listarPorEspecieYEstado(
            Especie especie,
            EstadoMascota estado
    );
}
