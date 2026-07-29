package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.response.MascotaResponse;
import com.huellitasoaxaca.backend.dto.response.MascotaCatalogoResponse;
import com.huellitasoaxaca.backend.dto.response.MascotaDetalleResponse;
import com.huellitasoaxaca.backend.entity.enums.Especie;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;
import com.huellitasoaxaca.backend.entity.enums.SexoMascota;
import com.huellitasoaxaca.backend.entity.enums.TamanoMascota;

import org.springframework.data.domain.Page;

import java.util.List;

public interface MascotaService 
{
    MascotaDetalleResponse obtenerDetallePublico(Long id);

    Page<MascotaCatalogoResponse> listarCatalogo(
            String nombre,
            Especie especie,
            SexoMascota sexo,
            TamanoMascota tamano,
            Integer edad,
            Long refugioId,
            int page,
            int size,
            String sort
    );

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
