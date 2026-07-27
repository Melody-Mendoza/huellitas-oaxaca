package com.huellitasoaxaca.backend.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.response.RolResponse;
import com.huellitasoaxaca.backend.entity.Rol;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.RolMapper;
import com.huellitasoaxaca.backend.repository.RolRepository;
import com.huellitasoaxaca.backend.services.RolService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RolServiceImpl implements RolService
{
        private final RolRepository rolRepository;
        private final RolMapper rolMapper;

        @Override
        public List<RolResponse> listarTodos() 
        {
                return rolRepository.findAll()
                        .stream()
                        .map(rolMapper::toResponse)
                        .toList();
        }

        @Override
        public RolResponse obtenerPorId(Long id) 
        {
                Rol rol = rolRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "No se encontró el rol con ID " + id
                                )
                        );

                return rolMapper.toResponse(rol);
        }

        @Override
        public RolResponse obtenerPorNombre(String nombre) 
        {
                Rol rol = rolRepository.findByNombre(nombre)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "No se encontró el rol " + nombre
                                )
                        );

                return rolMapper.toResponse(rol);
        }
}
