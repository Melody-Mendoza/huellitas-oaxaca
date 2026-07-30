package com.huellitasoaxaca.backend.services.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.huellitasoaxaca.backend.dto.response.ImagenMascotaResponse;
import com.huellitasoaxaca.backend.entity.ImagenMascota;
import com.huellitasoaxaca.backend.entity.Mascota;
import com.huellitasoaxaca.backend.entity.Refugio;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.exception.ReglaNegocioException;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.ImagenMascotaMapper;
import com.huellitasoaxaca.backend.repository.ImagenMascotaRepository;
import com.huellitasoaxaca.backend.repository.MascotaRepository;
import com.huellitasoaxaca.backend.repository.RefugioRepository;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
import com.huellitasoaxaca.backend.services.ImagenMascotaService;
import com.huellitasoaxaca.backend.services.MascotaImagenStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ImagenMascotaServiceImpl implements ImagenMascotaService
{
    private static final long CANTIDAD_MAXIMA = 8;

    private final ImagenMascotaRepository imagenRepository;
    private final MascotaRepository mascotaRepository;
    private final RefugioRepository refugioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ImagenMascotaMapper imagenMapper;
    private final MascotaImagenStorageService storageService;

    @Override
    @Transactional
    public ImagenMascotaResponse guardarPropia(
            Long refugioId,
            Long mascotaId,
            MultipartFile imagen,
            String correoAutenticado
    )
    {
        Mascota mascota = obtenerMascotaPropiaParaActualizar(
                refugioId,
                mascotaId,
                correoAutenticado
        );
        long cantidad = imagenRepository.countByMascotaId(mascotaId);

        if (cantidad >= CANTIDAD_MAXIMA)
        {
            throw new ReglaNegocioException(
                    "La mascota no puede tener más de 8 imágenes"
            );
        }

        String url = storageService.guardar(imagen);
        registrarLimpiezaSiRevierte(url);
        boolean principal = cantidad == 0;
        ImagenMascota nueva = ImagenMascota.builder()
                .url(url)
                .principal(principal)
                .mascota(mascota)
                .build();

        if (principal)
        {
            mascota.setImagen(url);
            mascotaRepository.saveAndFlush(mascota);
        }

        return imagenMapper.toResponse(imagenRepository.saveAndFlush(nueva));
    }

    @Override
    public List<ImagenMascotaResponse> listarPropias(
            Long refugioId,
            Long mascotaId,
            String correoAutenticado
    )
    {
        validarMascotaPropia(
                refugioId,
                mascotaId,
                correoAutenticado
        );

        return imagenRepository
                .findByMascotaIdOrderByPrincipalDescIdAsc(mascotaId)
                .stream()
                .map(imagenMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ImagenMascotaResponse establecerPrincipalPropia(
            Long refugioId,
            Long mascotaId,
            Long imagenId,
            String correoAutenticado
    )
    {
        Mascota mascota = obtenerMascotaPropiaParaActualizar(
                refugioId,
                mascotaId,
                correoAutenticado
        );
        ImagenMascota seleccionada = imagenRepository
                .findByIdAndMascotaId(imagenId, mascotaId)
                .orElseThrow(this::imagenPropiaNoEncontrada);

        if (Boolean.TRUE.equals(seleccionada.getPrincipal()))
        {
            return imagenMapper.toResponse(seleccionada);
        }

        List<ImagenMascota> imagenes = imagenRepository
                .findByMascotaIdOrderByPrincipalDescIdAsc(mascotaId);
        imagenes.forEach(actual -> actual.setPrincipal(
                actual.getId().equals(imagenId)
        ));
        imagenRepository.saveAll(imagenes);
        mascota.setImagen(seleccionada.getUrl());
        mascotaRepository.saveAndFlush(mascota);

        return imagenMapper.toResponse(seleccionada);
    }

    @Override
    @Transactional
    public void eliminarPropia(
            Long refugioId,
            Long mascotaId,
            Long imagenId,
            String correoAutenticado
    )
    {
        Mascota mascota = obtenerMascotaPropiaParaActualizar(
                refugioId,
                mascotaId,
                correoAutenticado
        );
        ImagenMascota imagen = imagenRepository
                .findByIdAndMascotaId(imagenId, mascotaId)
                .orElseThrow(this::imagenPropiaNoEncontrada);

        if (Boolean.TRUE.equals(imagen.getPrincipal()))
        {
            ImagenMascota reemplazo = imagenRepository
                    .findByMascotaIdOrderByIdAsc(mascotaId)
                    .stream()
                    .filter(actual -> !actual.getId().equals(imagenId))
                    .findFirst()
                    .orElse(null);

            if (reemplazo == null)
            {
                mascota.setImagen(null);
            }
            else
            {
                reemplazo.setPrincipal(true);
                imagenRepository.save(reemplazo);
                mascota.setImagen(reemplazo.getUrl());
            }
            mascotaRepository.save(mascota);
        }

        String url = imagen.getUrl();
        imagenRepository.delete(imagen);
        imagenRepository.flush();
        registrarEliminacionTrasCommit(url);
    }

    @Override
    public List<ImagenMascotaResponse> listarPorMascota(Long mascotaId) 
    {
        return imagenRepository.findByMascotaId(mascotaId)
                .stream()
                .map(imagenMapper::toResponse)
                .toList();
    }

    @Override
    public ImagenMascotaResponse obtenerPorId(Long id) 
    {
        ImagenMascota imagen = imagenRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró la imagen con ID " + id
                        )
                );

        return imagenMapper.toResponse(imagen);
    }

    @Override
    @Transactional
    public void eliminarPorMascota(Long mascotaId) 
    {
        imagenRepository.deleteByMascotaId(mascotaId);
    }

    private Mascota obtenerMascotaPropiaParaActualizar(
            Long refugioId,
            Long mascotaId,
            String correoAutenticado
    )
    {
        Usuario usuario = obtenerResponsableActivo(correoAutenticado);
        obtenerRefugioPropioActivo(refugioId, usuario.getId());

        return mascotaRepository
                .findPropiaParaActualizar(mascotaId, refugioId)
                .orElseThrow(this::mascotaPropiaNoEncontrada);
    }

    private void validarMascotaPropia(
            Long refugioId,
            Long mascotaId,
            String correoAutenticado
    )
    {
        Usuario usuario = obtenerResponsableActivo(correoAutenticado);
        obtenerRefugioPropioActivo(refugioId, usuario.getId());
        mascotaRepository.findByIdAndRefugioId(mascotaId, refugioId)
                .orElseThrow(this::mascotaPropiaNoEncontrada);
    }

    private Usuario obtenerResponsableActivo(String correoAutenticado)
    {
        Usuario usuario = usuarioRepository
                .findByCorreoAndActivoTrue(
                        correoAutenticado.trim().toLowerCase(Locale.ROOT)
                )
                .orElseThrow(() -> new AccessDeniedException(
                        "El usuario no puede administrar imágenes"
                ));

        if (!"REFUGIO".equals(usuario.getRol().getNombre()))
        {
            throw new AccessDeniedException(
                    "El rol no puede administrar imágenes"
            );
        }

        return usuario;
    }

    private Refugio obtenerRefugioPropioActivo(Long refugioId, Long usuarioId)
    {
        Refugio refugio = refugioRepository
                .findByIdAndUsuarioId(refugioId, usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el refugio solicitado"
                ));

        if (!Boolean.TRUE.equals(refugio.getActivo()))
        {
            throw new AccessDeniedException("El refugio no está activo");
        }

        return refugio;
    }

    private void registrarLimpiezaSiRevierte(String url)
    {
        try
        {
            verificarSincronizacionActiva();
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization()
                    {
                        @Override
                        public void afterCompletion(int status)
                        {
                            if (status != STATUS_COMMITTED)
                            {
                                eliminarArchivoSinPropagar(url);
                            }
                        }
                    }
            );
        }
        catch (RuntimeException exception)
        {
            eliminarArchivoSinPropagar(url);
            throw exception;
        }
    }

    private void registrarEliminacionTrasCommit(String url)
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
                            eliminarArchivoSinPropagar(url);
                        }
                    }
                }
        );
    }

    private void verificarSincronizacionActiva()
    {
        if (!TransactionSynchronizationManager.isSynchronizationActive())
        {
            throw new IllegalStateException(
                    "No existe una transacción activa para gestionar la imagen"
            );
        }
    }

    private void eliminarArchivoSinPropagar(String url)
    {
        try
        {
            storageService.eliminarSiExiste(url);
        }
        catch (RuntimeException exception)
        {
            log.error(
                    "No fue posible limpiar una imagen de mascota después de completar la transacción",
                    exception
            );
        }
    }

    private RecursoNoEncontradoException mascotaPropiaNoEncontrada()
    {
        return new RecursoNoEncontradoException(
                "No se encontró la mascota solicitada"
        );
    }

    private RecursoNoEncontradoException imagenPropiaNoEncontrada()
    {
        return new RecursoNoEncontradoException(
                "No se encontró la imagen solicitada"
        );
    }
}
