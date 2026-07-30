package com.huellitasoaxaca.backend.services.impl;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.response.FavoritoResponse;
import com.huellitasoaxaca.backend.entity.Favorito;
import com.huellitasoaxaca.backend.entity.FavoritoId;
import com.huellitasoaxaca.backend.entity.Mascota;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;
import com.huellitasoaxaca.backend.exception.FavoritoDuplicadoException;
import com.huellitasoaxaca.backend.exception.ParametroInvalidoException;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.FavoritoMapper;
import com.huellitasoaxaca.backend.repository.FavoritoRepository;
import com.huellitasoaxaca.backend.repository.MascotaRepository;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
import com.huellitasoaxaca.backend.services.FavoritoService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoritoServiceImpl implements FavoritoService
{
    private static final int TAMANO_MAXIMO_PAGINA = 50;

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MascotaRepository mascotaRepository;
    private final FavoritoMapper favoritoMapper;

    @Override
    @Transactional
    public FavoritoResponse agregar(
            Long mascotaId,
            String correoAutenticado
    )
    {
        Usuario usuario = obtenerUsuario(correoAutenticado);
        FavoritoId favoritoId = new FavoritoId(usuario.getId(), mascotaId);

        if (favoritoRepository.existsById(favoritoId))
        {
            throw favoritoDuplicado();
        }

        Mascota mascota = mascotaRepository
                .findByIdAndEstadoAndRefugioActivoTrueAndRefugioAprobadoTrue(
                        mascotaId,
                        EstadoMascota.DISPONIBLE
                )
                .orElseThrow(this::mascotaNoEncontrada);

        Favorito favorito = Favorito.builder()
                .id(favoritoId)
                .usuario(usuario)
                .mascota(mascota)
                .fechaAgregado(LocalDateTime.now())
                .build();

        try
        {
            return favoritoMapper.toResponse(
                    favoritoRepository.saveAndFlush(favorito)
            );
        }
        catch (DataIntegrityViolationException exception)
        {
            throw favoritoDuplicado();
        }
    }

    @Override
    public Page<FavoritoResponse> listar(
            String correoAutenticado,
            int page,
            int size
    )
    {
        validarPaginacion(page, size);
        Usuario usuario = obtenerUsuario(correoAutenticado);

        return favoritoRepository
                .findPaginaPorUsuario(
                        usuario.getId(),
                        PageRequest.of(page, size)
                )
                .map(favoritoMapper::toResponse);
    }

    @Override
    @Transactional
    public void eliminar(Long mascotaId, String correoAutenticado)
    {
        Usuario usuario = obtenerUsuario(correoAutenticado);
        favoritoRepository.deleteById(
                new FavoritoId(usuario.getId(), mascotaId)
        );
    }

    private Usuario obtenerUsuario(String correoAutenticado)
    {
        Usuario usuario = usuarioRepository
                .findByCorreoAndActivoTrue(
                        correoAutenticado
                                .trim()
                                .toLowerCase(Locale.ROOT)
                )
                .orElseThrow(() -> new AccessDeniedException(
                        "El usuario no puede gestionar favoritos"
                ));

        if (!"USUARIO".equals(usuario.getRol().getNombre()))
        {
            throw new AccessDeniedException(
                    "El rol no puede gestionar favoritos"
            );
        }

        return usuario;
    }

    private void validarPaginacion(int page, int size)
    {
        if (page < 0)
        {
            throw new ParametroInvalidoException(
                    "page no puede ser negativo"
            );
        }

        if (size < 1 || size > TAMANO_MAXIMO_PAGINA)
        {
            throw new ParametroInvalidoException(
                    "size debe estar entre 1 y 50"
            );
        }
    }

    private RecursoNoEncontradoException mascotaNoEncontrada()
    {
        return new RecursoNoEncontradoException(
                "No se encontró la mascota solicitada"
        );
    }

    private FavoritoDuplicadoException favoritoDuplicado()
    {
        return new FavoritoDuplicadoException(
                "La mascota ya está en favoritos"
        );
    }
}
