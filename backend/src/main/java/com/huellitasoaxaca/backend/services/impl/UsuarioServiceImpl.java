package com.huellitasoaxaca.backend.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.response.UsuarioResponse;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.UsuarioMapper;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
import com.huellitasoaxaca.backend.services.UsuarioService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService
{
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public List<UsuarioResponse> listarTodos() 
    {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Override
    public UsuarioResponse obtenerPorId(Long id) 
    {
        Usuario usuario = buscarEntidadPorId(id);
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    public UsuarioResponse obtenerPorCorreo(String correo) 
    {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró un usuario con el correo " + correo
                        )
                );

        return usuarioMapper.toResponse(usuario);
    }

    @Override
    public List<UsuarioResponse> listarActivos() 
    {
        return usuarioRepository.findByActivoTrue()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void desactivar(Long id) 
    {
        Usuario usuario = buscarEntidadPorId(id);

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    private Usuario buscarEntidadPorId(Long id) 
    {
        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró el usuario con ID " + id
                        )
                );
    }
}
