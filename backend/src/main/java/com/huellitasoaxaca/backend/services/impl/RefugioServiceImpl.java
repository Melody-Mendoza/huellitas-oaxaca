package com.huellitasoaxaca.backend.services.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.request.RefugioPerfilActualizarRequest;
import com.huellitasoaxaca.backend.dto.response.RefugioPanelResponse;
import com.huellitasoaxaca.backend.dto.response.RefugioPerfilResponse;
import com.huellitasoaxaca.backend.dto.response.RefugioResponse;
import com.huellitasoaxaca.backend.entity.Refugio;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;
import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.RefugioMapper;
import com.huellitasoaxaca.backend.repository.MascotaRepository;
import com.huellitasoaxaca.backend.repository.RefugioRepository;
import com.huellitasoaxaca.backend.repository.SolicitudAdopcionRepository;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
import com.huellitasoaxaca.backend.services.RefugioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefugioServiceImpl implements RefugioService
{
    private final RefugioRepository refugioRepository;
    private final UsuarioRepository usuarioRepository;
    private final MascotaRepository mascotaRepository;
    private final SolicitudAdopcionRepository solicitudRepository;
    private final RefugioMapper refugioMapper;

    @Override
    public List<RefugioPerfilResponse> listarPerfilesPropios(
            String correoAutenticado
    )
    {
        Usuario usuario = obtenerResponsableActivo(correoAutenticado);

        return refugioRepository
                .findByUsuarioIdAndActivoTrueOrderByNombreAscIdAsc(
                        usuario.getId()
                )
                .stream()
                .map(refugioMapper::toPerfilResponse)
                .toList();
    }

    @Override
    public RefugioPerfilResponse obtenerPerfilPropio(
            Long refugioId,
            String correoAutenticado
    )
    {
        Usuario usuario = obtenerResponsableActivo(correoAutenticado);
        Refugio refugio = obtenerRefugioPropio(
                refugioId,
                usuario.getId()
        );
        validarRefugioActivo(refugio);

        return refugioMapper.toPerfilResponse(refugio);
    }

    @Override
    @Transactional
    public RefugioPerfilResponse actualizarPerfilPropio(
            Long refugioId,
            RefugioPerfilActualizarRequest request,
            String correoAutenticado
    )
    {
        Usuario usuario = obtenerResponsableActivo(correoAutenticado);
        Refugio refugio = obtenerRefugioPropio(
                refugioId,
                usuario.getId()
        );
        validarRefugioActivo(refugio);

        aplicarCambiosPermitidos(refugio, request);

        return refugioMapper.toPerfilResponse(
                refugioRepository.save(refugio)
        );
    }

    @Override
    public RefugioPanelResponse obtenerPanelPropio(
            Long refugioId,
            String correoAutenticado
    )
    {
        Usuario usuario = obtenerResponsableActivo(correoAutenticado);
        Refugio refugio = obtenerRefugioPropio(
                refugioId,
                usuario.getId()
        );
        validarRefugioActivo(refugio);

        return new RefugioPanelResponse(
                mascotaRepository.countByRefugioId(refugioId),
                mascotaRepository.countByRefugioIdAndEstado(
                        refugioId,
                        EstadoMascota.DISPONIBLE
                ),
                mascotaRepository.countByRefugioIdAndEstado(
                        refugioId,
                        EstadoMascota.EN_PROCESO
                ),
                mascotaRepository.countByRefugioIdAndEstado(
                        refugioId,
                        EstadoMascota.ADOPTADO
                ),
                solicitudRepository.countSolicitudesPorRefugio(
                        refugioId
                ),
                solicitudRepository.countSolicitudesPorRefugioYEstado(
                        refugioId,
                        EstadoSolicitud.PENDIENTE
                ),
                solicitudRepository.countSolicitudesPorRefugioYEstado(
                        refugioId,
                        EstadoSolicitud.APROBADA
                ),
                solicitudRepository.countSolicitudesPorRefugioYEstado(
                        refugioId,
                        EstadoSolicitud.RECHAZADA
                )
        );
    }

    @Override
    public List<RefugioResponse> listarTodos() 
    {
        return refugioRepository.findAll()
                .stream()
                .map(refugioMapper::toResponse)
                .toList();
    }

    @Override
    public List<RefugioResponse> listarActivos() 
    {
        return refugioRepository.findByActivoTrue()
                .stream()
                .map(refugioMapper::toResponse)
                .toList();
    }

    @Override
    public RefugioResponse obtenerPorId(Long id) {
        return refugioMapper.toResponse(buscarEntidadPorId(id));
    }

    @Override
    public RefugioResponse obtenerPorNombre(String nombre) 
    {
        Refugio refugio = refugioRepository.findByNombre(nombre)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró el refugio " + nombre
                        )
                );

        return refugioMapper.toResponse(refugio);
    }

    @Override
    public List<RefugioResponse> listarPorUsuario(Long usuarioId) 
    {
        List<Refugio> refugios = refugioRepository.findByUsuarioId(usuarioId);

        if (refugios.isEmpty()) 
        {
            throw new RecursoNoEncontradoException(
                    "El usuario no tiene refugios asignados"
            );
        }

        return refugios.stream()
                .map(refugioMapper::toResponse)
                .toList();
    }

    private Refugio buscarEntidadPorId(Long id) 
    {
        return refugioRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró el refugio con ID " + id
                        )
                );
    }

    private Usuario obtenerResponsableActivo(String correoAutenticado)
    {
        Usuario usuario = usuarioRepository
                .findByCorreoAndActivoTrue(
                        correoAutenticado
                                .trim()
                                .toLowerCase(Locale.ROOT)
                )
                .orElseThrow(() -> new AccessDeniedException(
                        "El usuario no puede administrar refugios"
                ));

        if (!"REFUGIO".equals(usuario.getRol().getNombre()))
        {
            throw new AccessDeniedException(
                    "El rol no puede administrar refugios"
            );
        }

        return usuario;
    }

    private Refugio obtenerRefugioPropio(
            Long refugioId,
            Long usuarioId
    )
    {
        return refugioRepository
                .findByIdAndUsuarioId(refugioId, usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el refugio solicitado"
                ));
    }

    private void validarRefugioActivo(Refugio refugio)
    {
        if (!Boolean.TRUE.equals(refugio.getActivo())
                || !Boolean.TRUE.equals(refugio.getAprobado()))
        {
            throw new AccessDeniedException(
                    "El refugio no está autorizado para operar"
            );
        }
    }

    private void aplicarCambiosPermitidos(
            Refugio refugio,
            RefugioPerfilActualizarRequest request
    )
    {
        if (request.nombre() != null)
        {
            refugio.setNombre(normalizarNombre(request.nombre()));
        }
        if (request.descripcion() != null)
        {
            refugio.setDescripcion(normalizarOpcional(request.descripcion()));
        }
        if (request.direccion() != null)
        {
            refugio.setDireccion(normalizarOpcional(request.direccion()));
        }
        if (request.telefono() != null)
        {
            refugio.setTelefono(normalizarOpcional(request.telefono()));
        }
        if (request.correo() != null)
        {
            String correo = normalizarOpcional(request.correo());
            refugio.setCorreo(
                    correo != null
                            ? correo.toLowerCase(Locale.ROOT)
                            : null
            );
        }
    }

    private String normalizarNombre(String valor)
    {
        return valor.trim().replaceAll("\\s+", " ");
    }

    private String normalizarOpcional(String valor)
    {
        String normalizado = valor.trim();
        return normalizado.isEmpty() ? null : normalizado;
    }
}
