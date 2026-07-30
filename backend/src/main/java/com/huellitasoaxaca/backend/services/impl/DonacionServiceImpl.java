package com.huellitasoaxaca.backend.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.request.DonacionCrearRequest;
import com.huellitasoaxaca.backend.dto.response.DonacionResponse;
import com.huellitasoaxaca.backend.entity.Donacion;
import com.huellitasoaxaca.backend.entity.Refugio;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.entity.enums.EstatusDonacion;
import com.huellitasoaxaca.backend.exception.ConflictoDonacionException;
import com.huellitasoaxaca.backend.exception.ParametroInvalidoException;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.DonacionMapper;
import com.huellitasoaxaca.backend.repository.DonacionRepository;
import com.huellitasoaxaca.backend.repository.RefugioRepository;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
import com.huellitasoaxaca.backend.services.DonacionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DonacionServiceImpl implements DonacionService
{
    private static final int TAMANO_MAXIMO_PAGINA = 50;
    private static final Pattern CLAVE_IDEMPOTENCIA = Pattern.compile(
            "^[A-Za-z0-9_-]{8,64}$"
    );

    private final DonacionRepository donacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final RefugioRepository refugioRepository;
    private final DonacionMapper donacionMapper;

    @Override
    @Transactional
    public ResultadoCreacion crear(
            DonacionCrearRequest request,
            String claveIdempotencia,
            String correoAutenticado
    )
    {
        validarClaveIdempotencia(claveIdempotencia);
        Usuario usuario = obtenerUsuarioParaActualizar(correoAutenticado);
        String mensaje = normalizarMensaje(request.mensaje());

        Donacion existente = donacionRepository
                .findByUsuarioIdAndClaveIdempotencia(
                        usuario.getId(),
                        claveIdempotencia
                )
                .orElse(null);

        if (existente != null)
        {
            validarMismoContenido(existente, request, mensaje);
            return new ResultadoCreacion(
                    donacionMapper.toResponse(existente),
                    false
            );
        }

        Refugio refugio = obtenerRefugioActivo(request.refugioId());
        LocalDateTime ahora = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Donacion donacion = Donacion.builder()
                .monto(request.monto())
                .metodoPago(request.metodoPago())
                .fecha(ahora)
                .fechaActualizacion(ahora)
                .estatus(EstatusDonacion.PENDIENTE)
                .mensaje(mensaje)
                .claveIdempotencia(claveIdempotencia)
                .usuario(usuario)
                .refugio(refugio)
                .build();

        return new ResultadoCreacion(
                donacionMapper.toResponse(
                        donacionRepository.saveAndFlush(donacion)
                ),
                true
        );
    }

    @Override
    public Page<DonacionResponse> listarPropias(
            String correoAutenticado,
            int page,
            int size
    )
    {
        validarPaginacion(page, size);
        Usuario usuario = obtenerUsuario(correoAutenticado);

        return donacionRepository
                .findPaginaPropia(
                        usuario.getId(),
                        PageRequest.of(page, size)
                )
                .map(donacionMapper::toResponse);
    }

    @Override
    public DonacionResponse obtenerPropia(
            Long donacionId,
            String correoAutenticado
    )
    {
        Usuario usuario = obtenerUsuario(correoAutenticado);
        return donacionMapper.toResponse(
                donacionRepository
                        .findByIdAndUsuarioId(donacionId, usuario.getId())
                        .orElseThrow(this::donacionNoEncontrada)
        );
    }

    @Override
    @Transactional
    public DonacionResponse confirmar(
            Long donacionId,
            String correoAutenticado
    )
    {
        return cambiarEstado(
                donacionId,
                correoAutenticado,
                EstatusDonacion.COMPLETADA
        );
    }

    @Override
    @Transactional
    public DonacionResponse cancelar(
            Long donacionId,
            String correoAutenticado
    )
    {
        return cambiarEstado(
                donacionId,
                correoAutenticado,
                EstatusDonacion.CANCELADA
        );
    }

    private DonacionResponse cambiarEstado(
            Long donacionId,
            String correoAutenticado,
            EstatusDonacion nuevoEstado
    )
    {
        Usuario usuario = obtenerUsuario(correoAutenticado);
        Donacion donacion = donacionRepository
                .findPropiaParaActualizar(donacionId, usuario.getId())
                .orElseThrow(this::donacionNoEncontrada);

        if (donacion.getEstatus() != EstatusDonacion.PENDIENTE)
        {
            throw new ConflictoDonacionException(
                    "La donación simulada ya tiene un estado terminal"
            );
        }

        donacion.setEstatus(nuevoEstado);
        donacion.setFechaActualizacion(LocalDateTime.now());
        return donacionMapper.toResponse(donacionRepository.save(donacion));
    }

    private Usuario obtenerUsuario(String correoAutenticado)
    {
        Usuario usuario = usuarioRepository
                .findByCorreoAndActivoTrue(normalizarCorreo(correoAutenticado))
                .orElseThrow(() -> new AccessDeniedException(
                        "El usuario no puede gestionar donaciones"
                ));
        validarRolUsuario(usuario);
        return usuario;
    }

    private Usuario obtenerUsuarioParaActualizar(String correoAutenticado)
    {
        Usuario usuario = usuarioRepository
                .findActivoPorCorreoParaActualizar(
                        normalizarCorreo(correoAutenticado)
                )
                .orElseThrow(() -> new AccessDeniedException(
                        "El usuario no puede gestionar donaciones"
                ));
        validarRolUsuario(usuario);
        return usuario;
    }

    private void validarRolUsuario(Usuario usuario)
    {
        if (!"USUARIO".equals(usuario.getRol().getNombre()))
        {
            throw new AccessDeniedException(
                    "El rol no puede gestionar donaciones"
            );
        }
    }

    private Refugio obtenerRefugioActivo(Long refugioId)
    {
        Refugio refugio = refugioRepository
                .findById(refugioId)
                .orElseThrow(this::refugioNoEncontrado);

        if (!Boolean.TRUE.equals(refugio.getActivo())
                || !Boolean.TRUE.equals(refugio.getAprobado()))
        {
            throw refugioNoEncontrado();
        }
        return refugio;
    }

    private void validarMismoContenido(
            Donacion existente,
            DonacionCrearRequest request,
            String mensajeNormalizado
    )
    {
        boolean mismoMonto = existente.getMonto().compareTo(request.monto()) == 0;
        boolean mismoContenido = mismoMonto
                && existente.getRefugio().getId().equals(request.refugioId())
                && existente.getMetodoPago() == request.metodoPago()
                && java.util.Objects.equals(
                        existente.getMensaje(),
                        mensajeNormalizado
                );

        if (!mismoContenido)
        {
            throw new ConflictoDonacionException(
                    "La clave de idempotencia ya fue utilizada con otros datos"
            );
        }
    }

    private void validarClaveIdempotencia(String claveIdempotencia)
    {
        if (claveIdempotencia == null
                || !CLAVE_IDEMPOTENCIA.matcher(claveIdempotencia).matches())
        {
            throw new ParametroInvalidoException(
                    "Idempotency-Key debe tener entre 8 y 64 caracteres válidos"
            );
        }
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

    private String normalizarCorreo(String correo)
    {
        return correo.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizarMensaje(String mensaje)
    {
        if (mensaje == null || mensaje.isBlank())
        {
            return null;
        }
        return mensaje.trim();
    }

    private RecursoNoEncontradoException refugioNoEncontrado()
    {
        return new RecursoNoEncontradoException(
                "No se encontró el refugio solicitado"
        );
    }

    private RecursoNoEncontradoException donacionNoEncontrada()
    {
        return new RecursoNoEncontradoException(
                "No se encontró la donación solicitada"
        );
    }
}
