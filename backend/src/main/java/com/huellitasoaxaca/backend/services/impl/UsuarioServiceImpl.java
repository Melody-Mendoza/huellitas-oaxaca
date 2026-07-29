package com.huellitasoaxaca.backend.services.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.huellitasoaxaca.backend.dto.request.CambiarPasswordRequest;
import com.huellitasoaxaca.backend.dto.request.UsuarioActualizarRequest;
import com.huellitasoaxaca.backend.dto.response.UsuarioResponse;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.exception.ReglaNegocioException;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.UsuarioMapper;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
import com.huellitasoaxaca.backend.services.PerfilFotoStorageService;
import com.huellitasoaxaca.backend.services.UsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService
{
        private final UsuarioRepository usuarioRepository;
        private final UsuarioMapper usuarioMapper;
        private final PasswordEncoder passwordEncoder;
        private final PerfilFotoStorageService perfilFotoStorageService;

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
        public UsuarioResponse obtenerActivoPorCorreo(String correo)
        {
                Usuario usuario = usuarioRepository.findByCorreoAndActivoTrue(
                                correo.trim().toLowerCase()
                        )
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "No se encontró el usuario autenticado"
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

        @Override
        @Transactional
        public UsuarioResponse actualizarPerfil(
                String correoAutenticado,
                UsuarioActualizarRequest request
        )
        {
                Usuario usuario = usuarioRepository
                        .findByCorreoAndActivoTrue(
                                correoAutenticado.trim().toLowerCase()
                        )
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "No se encontró el usuario autenticado"
                                )
                        );

                usuario.setNombre(request.nombre().trim());
                usuario.setApellidoPaterno(
                        request.apellidoPaterno().trim()
                );
                usuario.setApellidoMaterno(
                        limpiarTextoOpcional(request.apellidoMaterno())
                );
                usuario.setTelefono(
                        limpiarTextoOpcional(request.telefono())
                );

                Usuario actualizado = usuarioRepository.save(usuario);

                return usuarioMapper.toResponse(actualizado);
        }

        @Override
        @Transactional
        public UsuarioResponse actualizarFotoPerfil(
                String correoAutenticado,
                MultipartFile foto
        )
        {
                Usuario usuario = buscarActivoParaActualizar(
                        correoAutenticado
                );
                String fotoAnterior = usuario.getFotoPerfil();
                String fotoNueva = perfilFotoStorageService.guardar(foto);

                registrarReemplazoTrasTransaccion(
                        fotoAnterior,
                        fotoNueva
                );

                usuario.setFotoPerfil(fotoNueva);

                return usuarioMapper.toResponse(
                        usuarioRepository.saveAndFlush(usuario)
                );
        }

        @Override
        @Transactional
        public UsuarioResponse eliminarFotoPerfil(
                String correoAutenticado
        )
        {
                Usuario usuario = buscarActivoParaActualizar(
                        correoAutenticado
                );
                String fotoAnterior = usuario.getFotoPerfil();

                if (fotoAnterior == null)
                {
                        return usuarioMapper.toResponse(usuario);
                }

                registrarEliminacionTrasCommit(fotoAnterior);
                usuario.setFotoPerfil(null);

                return usuarioMapper.toResponse(
                        usuarioRepository.saveAndFlush(usuario)
                );
        }

        @Override
        @Transactional
        public void cambiarPassword(
                String correoAutenticado,
                CambiarPasswordRequest request
        )
        {
                Usuario usuario = usuarioRepository
                        .findByCorreoAndActivoTrue(correoAutenticado.trim().toLowerCase())
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "No se encontró el usuario autenticado"
                                )
                        );

                if (!passwordEncoder.matches(request.passwordActual(),usuario.getPassword()))
                {
                        throw new ReglaNegocioException(
                                "La contraseña actual es incorrecta"
                        );
                }

                if (!request.nuevaPassword().equals(request.confirmarPassword()))
                {
                        throw new ReglaNegocioException(
                                "Las contraseñas nuevas no coinciden"
                        );
                }

                if (passwordEncoder.matches(request.nuevaPassword(),usuario.getPassword()))
                {
                        throw new ReglaNegocioException(
                                "La nueva contraseña debe ser diferente a la actual"
                        );
                }

                usuario.setPassword(passwordEncoder.encode(request.nuevaPassword()));

                usuarioRepository.save(usuario);
        }

        @Override
        @Transactional
        public UsuarioResponse cambiarEstado(
                Long id,
                Boolean activo
        )
        {
                Usuario usuario = buscarEntidadPorId(id);

                usuario.setActivo(activo);

                return usuarioMapper.toResponse(
                        usuarioRepository.save(usuario)
                );
        }

        private Usuario buscarActivoParaActualizar(
                String correoAutenticado
        )
        {
                return usuarioRepository
                        .findActivoPorCorreoParaActualizar(
                                correoAutenticado.trim().toLowerCase()
                        )
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "No se encontró el usuario autenticado"
                                )
                        );
        }

        private void registrarReemplazoTrasTransaccion(
                String fotoAnterior,
                String fotoNueva
        )
        {
                try
                {
                        verificarSincronizacionActiva();
                        TransactionSynchronizationManager
                                .registerSynchronization(
                                        new TransactionSynchronization()
                                        {
                                                @Override
                                                public void afterCompletion(
                                                        int status
                                                )
                                                {
                                                        if (status == STATUS_COMMITTED)
                                                        {
                                                                eliminarFotoSinPropagar(
                                                                        fotoAnterior
                                                                );
                                                        }
                                                        else
                                                        {
                                                                eliminarFotoSinPropagar(
                                                                        fotoNueva
                                                                );
                                                        }
                                                }
                                        }
                                );
                }
                catch (RuntimeException exception)
                {
                        eliminarFotoSinPropagar(fotoNueva);
                        throw exception;
                }
        }

        private void registrarEliminacionTrasCommit(
                String fotoAnterior
        )
        {
                verificarSincronizacionActiva();
                TransactionSynchronizationManager.registerSynchronization(
                        new TransactionSynchronization()
                        {
                                @Override
                                public void afterCompletion(int status)
                                {
                                        if (status == STATUS_COMMITTED)
                                        {
                                                eliminarFotoSinPropagar(
                                                        fotoAnterior
                                                );
                                        }
                                }
                        }
                );
        }

        private void verificarSincronizacionActiva()
        {
                if (!TransactionSynchronizationManager
                        .isSynchronizationActive())
                {
                        throw new IllegalStateException(
                                "No existe una transacción activa para actualizar la foto"
                        );
                }
        }

        private void eliminarFotoSinPropagar(String fotoPerfil)
        {
                if (fotoPerfil == null)
                {
                        return;
                }

                try
                {
                        perfilFotoStorageService.eliminarSiExiste(fotoPerfil);
                }
                catch (RuntimeException exception)
                {
                        log.error(
                                "No fue posible limpiar una foto de perfil después de completar la transacción",
                                exception
                        );
                }
        }

        private String limpiarTextoOpcional(String valor)
        {
                if (valor == null || valor.isBlank())
                {
                        return null;
                }

                return valor.trim();
        }
}
