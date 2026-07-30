package com.huellitasoaxaca.backend.services.impl;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.response.RefugioDonacionResponse;
import com.huellitasoaxaca.backend.entity.Refugio;
import com.huellitasoaxaca.backend.repository.RefugioRepository;
import com.huellitasoaxaca.backend.services.RefugioDonacionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefugioDonacionServiceImpl implements RefugioDonacionService
{
    private final RefugioRepository refugioRepository;

    @Override
    public List<RefugioDonacionResponse> listarDisponibles()
    {
        return refugioRepository.findByActivoTrue()
                .stream()
                .sorted(
                        Comparator.comparing(
                                Refugio::getNombre,
                                String.CASE_INSENSITIVE_ORDER
                        ).thenComparing(Refugio::getId)
                )
                .map(refugio -> new RefugioDonacionResponse(
                        refugio.getId(),
                        refugio.getNombre()
                ))
                .toList();
    }
}
