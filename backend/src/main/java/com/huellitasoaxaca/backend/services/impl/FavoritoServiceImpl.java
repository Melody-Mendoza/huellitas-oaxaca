package com.huellitasoaxaca.backend.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.response.FavoritoResponse;
import com.huellitasoaxaca.backend.entity.Favorito;
import com.huellitasoaxaca.backend.mapper.FavoritoMapper;
import com.huellitasoaxaca.backend.repository.FavoritoRepository;
import com.huellitasoaxaca.backend.services.FavoritoService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoritoServiceImpl implements FavoritoService
{
    private final FavoritoRepository favoritoRepository;
    private final FavoritoMapper favoritoMapper;

    @Override
    public List<FavoritoResponse> listarPorUsuario(Long usuarioId) 
    {
        return convertirLista(
                favoritoRepository.findByUsuarioId(usuarioId)
        );
    }

    @Override
    public List<FavoritoResponse> listarPorMascota(Long mascotaId) 
    {
        return convertirLista(
                favoritoRepository.findByMascotaId(mascotaId)
        );
    }

    @Override
    public boolean existe(Long usuarioId, Long mascotaId) 
    {
        return favoritoRepository.existsByUsuarioIdAndMascotaId(
                usuarioId,
                mascotaId
        );
    }

    @Override
    @Transactional
    public void eliminar(Long usuarioId, Long mascotaId) 
    {
        favoritoRepository.deleteByUsuarioIdAndMascotaId(
                usuarioId,
                mascotaId
        );
    }

    private List<FavoritoResponse> convertirLista(
            List<Favorito> favoritos
    ) 
    {
        return favoritos.stream()
                .map(favoritoMapper::toResponse)
                .toList();
    }
}
