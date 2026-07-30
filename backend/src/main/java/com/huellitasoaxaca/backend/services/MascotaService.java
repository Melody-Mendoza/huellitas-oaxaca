package com.huellitasoaxaca.backend.services;

import com.huellitasoaxaca.backend.dto.response.MascotaResponse;
import com.huellitasoaxaca.backend.dto.request.MascotaCrearRequest;
import com.huellitasoaxaca.backend.dto.request.MascotaActualizarRequest;
import com.huellitasoaxaca.backend.dto.request.MascotaEstadoActualizarRequest;
import com.huellitasoaxaca.backend.dto.response.MascotaCatalogoResponse;
import com.huellitasoaxaca.backend.dto.response.MascotaDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.MascotaPropiaDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.MascotaPropiaResumenResponse;
import com.huellitasoaxaca.backend.entity.enums.Especie;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;
import com.huellitasoaxaca.backend.entity.enums.SexoMascota;
import com.huellitasoaxaca.backend.entity.enums.TamanoMascota;

import org.springframework.data.domain.Page;

import java.util.List;

public interface MascotaService 
{
    MascotaPropiaDetalleResponse crearPropia(
            Long refugioId,
            MascotaCrearRequest request,
            String correoAutenticado
    );

    Page<MascotaPropiaResumenResponse> listarPropias(
            Long refugioId,
            String nombre,
            Especie especie,
            EstadoMascota estado,
            int page,
            int size,
            String correoAutenticado
    );

    MascotaPropiaDetalleResponse obtenerDetallePropio(
            Long refugioId,
            Long mascotaId,
            String correoAutenticado
    );

    MascotaPropiaDetalleResponse actualizarPropia(
            Long refugioId,
            Long mascotaId,
            MascotaActualizarRequest request,
            String correoAutenticado
    );

    MascotaPropiaDetalleResponse actualizarEstadoPropio(
            Long refugioId,
            Long mascotaId,
            MascotaEstadoActualizarRequest request,
            String correoAutenticado
    );

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
