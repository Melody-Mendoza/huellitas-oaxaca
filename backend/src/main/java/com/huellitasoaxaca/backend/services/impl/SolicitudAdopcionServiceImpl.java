package com.huellitasoaxaca.backend.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.request.SolicitudAdopcionCrearRequest;
import com.huellitasoaxaca.backend.dto.response.SolicitudAdopcionResponse;
import com.huellitasoaxaca.backend.entity.HistorialEstado;
import com.huellitasoaxaca.backend.entity.Mascota;
import com.huellitasoaxaca.backend.entity.Refugio;
import com.huellitasoaxaca.backend.entity.SolicitudAdopcion;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;
import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.exception.SolicitudDuplicadaException;
import com.huellitasoaxaca.backend.mapper.SolicitudAdopcionMapper;
import com.huellitasoaxaca.backend.repository.HistorialEstadoRepository;
import com.huellitasoaxaca.backend.repository.MascotaRepository;
import com.huellitasoaxaca.backend.repository.SolicitudAdopcionRepository;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
import com.huellitasoaxaca.backend.services.SolicitudAdopcionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolicitudAdopcionServiceImpl implements SolicitudAdopcionService
{
    private final SolicitudAdopcionRepository solicitudRepository;
    private final HistorialEstadoRepository historialRepository;
    private final UsuarioRepository usuarioRepository;
    private final MascotaRepository mascotaRepository;
    private final SolicitudAdopcionMapper solicitudMapper;

    @Override
    @Transactional
    public SolicitudAdopcionResponse crear(
            SolicitudAdopcionCrearRequest request,
            String correoAutenticado
    )
    {
        Usuario usuario = usuarioRepository
                .findByCorreoAndActivoTrue(
                        correoAutenticado.trim().toLowerCase(Locale.ROOT)
                )
                .orElseThrow(() -> new AccessDeniedException(
                        "El usuario no puede crear solicitudes"
                ));

        if (!"USUARIO".equals(usuario.getRol().getNombre()))
        {
            throw new AccessDeniedException(
                    "El rol no puede crear solicitudes"
            );
        }

        Mascota mascota = mascotaRepository
                .findByIdParaSolicitud(request.mascotaId())
                .orElseThrow(this::mascotaNoEncontrada);

        validarMascotaPublicable(mascota);

        boolean duplicada = solicitudRepository
                .existsByUsuarioIdAndMascotaIdAndEstadoIn(
                        usuario.getId(),
                        mascota.getId(),
                        List.of(
                                EstadoSolicitud.PENDIENTE,
                                EstadoSolicitud.APROBADA
                        )
                );

        if (duplicada)
        {
            throw new SolicitudDuplicadaException(
                    "Ya existe una solicitud activa para esta mascota"
            );
        }

        LocalDateTime ahora = LocalDateTime.now();
        SolicitudAdopcion solicitud = SolicitudAdopcion.builder()
                .fechaSolicitud(ahora)
                .estado(EstadoSolicitud.PENDIENTE)
                .comentarios(limpiarComentario(request.comentarios()))
                .usuario(usuario)
                .mascota(mascota)
                .build();

        SolicitudAdopcion guardada = solicitudRepository.save(solicitud);

        historialRepository.save(HistorialEstado.builder()
                .solicitud(guardada)
                .estado(EstadoSolicitud.PENDIENTE)
                .fecha(ahora)
                .observaciones(null)
                .build());

        return solicitudMapper.toResponse(guardada);
    }

    @Override
    public List<SolicitudAdopcionResponse> listarTodas() {
        return convertirLista(solicitudRepository.findAll());
    }

    @Override
    public SolicitudAdopcionResponse obtenerPorId(Long id) {
        SolicitudAdopcion solicitud = solicitudRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró la solicitud con ID " + id
                        )
                );

        return solicitudMapper.toResponse(solicitud);
    }

    @Override
    public List<SolicitudAdopcionResponse> listarPorUsuario(Long usuarioId) {
        return convertirLista(
                solicitudRepository.findByUsuarioId(usuarioId)
        );
    }

    @Override
    public List<SolicitudAdopcionResponse> listarPorMascota(Long mascotaId) {
        return convertirLista(
                solicitudRepository.findByMascotaId(mascotaId)
        );
    }

    @Override
    public List<SolicitudAdopcionResponse> listarPorEstado(
            EstadoSolicitud estado
    ) {
        return convertirLista(
                solicitudRepository.findByEstado(estado)
        );
    }

    @Override
    public List<SolicitudAdopcionResponse> listarPorUsuarioYEstado(
            Long usuarioId,
            EstadoSolicitud estado
    ) {
        return convertirLista(
                solicitudRepository.findByUsuarioIdAndEstado(
                        usuarioId,
                        estado
                )
        );
    }

    private List<SolicitudAdopcionResponse> convertirLista(
            List<SolicitudAdopcion> solicitudes
    ) {
        return solicitudes.stream()
                .map(solicitudMapper::toResponse)
                .toList();
    }

    private void validarMascotaPublicable(Mascota mascota)
    {
        Refugio refugio = mascota.getRefugio();
        if (mascota.getEstado() != EstadoMascota.DISPONIBLE
                || refugio == null
                || !Boolean.TRUE.equals(refugio.getActivo()))
        {
            throw mascotaNoEncontrada();
        }
    }

    private RecursoNoEncontradoException mascotaNoEncontrada()
    {
        return new RecursoNoEncontradoException(
                "No se encontró la mascota solicitada"
        );
    }

    private String limpiarComentario(String comentario)
    {
        if (comentario == null || comentario.isBlank())
        {
            return null;
        }
        return comentario.trim();
    }
}
