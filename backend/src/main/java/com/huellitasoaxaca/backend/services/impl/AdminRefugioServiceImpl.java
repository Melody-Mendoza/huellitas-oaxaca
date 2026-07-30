package com.huellitasoaxaca.backend.services.impl;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.request.RefugioAdminCrearRequest;
import com.huellitasoaxaca.backend.dto.request.RefugioCompletoAdminCrearRequest;
import com.huellitasoaxaca.backend.dto.response.PaginaResponse;
import com.huellitasoaxaca.backend.dto.response.RefugioAdminDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.RefugioAdminResumenResponse;
import com.huellitasoaxaca.backend.entity.Refugio;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.entity.Rol;
import com.huellitasoaxaca.backend.entity.enums.TipoAccionAuditoria;
import com.huellitasoaxaca.backend.exception.ConflictoAdministrativoException;
import com.huellitasoaxaca.backend.exception.ParametroInvalidoException;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.exception.RecursoDuplicadoException;
import com.huellitasoaxaca.backend.mapper.RefugioMapper;
import com.huellitasoaxaca.backend.repository.RefugioRepository;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
import com.huellitasoaxaca.backend.services.AdminRefugioService;
import com.huellitasoaxaca.backend.services.AuditoriaAdministrativaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRefugioServiceImpl implements AdminRefugioService
{
    private static final int TAMANO_MAXIMO_PAGINA = 50;
    private static final Set<String> CAMPOS_ORDEN_PERMITIDOS = Set.of(
            "id",
            "nombre",
            "correo",
            "aprobado",
            "activo"
    );

    private final RefugioRepository refugioRepository;
    private final UsuarioRepository usuarioRepository;
    private final RefugioMapper refugioMapper;
    private final AuditoriaAdministrativaService auditoriaService;
    private final PasswordEncoder passwordEncoder;
    private final com.huellitasoaxaca.backend.repository.RolRepository rolRepository;

    @Override
    public PaginaResponse<RefugioAdminResumenResponse> listar(
            int page,
            int size,
            String sort,
            String texto,
            String aprobado,
            String activo,
            Long responsableId,
            String correoAdministrador
    )
    {
        validarAdministradorActivo(correoAdministrador);
        validarPaginacion(page, size);
        validarResponsableId(responsableId);

        Specification<Refugio> filtros = crearFiltros(
                texto,
                validarBooleano(aprobado, "aprobado"),
                validarBooleano(activo, "activo"),
                responsableId
        );
        Page<RefugioAdminResumenResponse> refugios = refugioRepository
                .findAll(
                        filtros,
                        PageRequest.of(page, size, crearOrden(sort))
                )
                .map(refugioMapper::toAdminResumen);

        return PaginaResponse.desde(refugios);
    }

    @Override
    @Transactional
    public RefugioAdminDetalleResponse crear(
            RefugioAdminCrearRequest request,
            String correoAdministrador
    )
    {
        Usuario administrador = bloquearAdministradorActivo(
                correoAdministrador
        );
        Usuario responsable = obtenerResponsableValido(
                request.responsableId()
        );
        String motivo = validarMotivo(request.motivo());

        Refugio refugio = Refugio.builder()
                .nombre(normalizarTexto(request.nombre()))
                .descripcion(normalizarTexto(request.descripcion()))
                .direccion(normalizarTexto(request.direccion()))
                .telefono(request.telefono().trim())
                .correo(normalizarCorreo(request.correo()))
                .activo(false)
                .aprobado(false)
                .fechaAprobacion(null)
                .aprobadoPor(null)
                .usuario(responsable)
                .build();

        Refugio guardado = refugioRepository.save(refugio);
        auditoriaService.registrarAccionRefugio(
                administrador,
                guardado,
                TipoAccionAuditoria.CREAR_REFUGIO,
                motivo,
                Map.of(),
                estadoCompleto(guardado),
                Map.of("responsableId", responsable.getId().toString())
        );

        return refugioMapper.toAdminDetalle(guardado);
    }

    @Override
    @Transactional
    public RefugioAdminDetalleResponse crearCompleto(
            RefugioCompletoAdminCrearRequest request,
            String correoAdministrador
    )
    {
        Usuario administrador = bloquearAdministradorActivo(
                correoAdministrador
        );
        String correoResponsable = normalizarCorreo(
                request.responsableCorreo()
        );
        String correoRefugio = normalizarCorreo(request.correo());

        if (usuarioRepository.existsByCorreo(correoResponsable))
        {
            throw new RecursoDuplicadoException(
                    "Ya existe un usuario con el correo del responsable"
            );
        }
        if (refugioRepository.existsByNombre(request.nombre().trim()))
        {
            throw new RecursoDuplicadoException(
                    "Ya existe un refugio con ese nombre"
            );
        }

        Rol rolRefugio = rolRepository.findByNombre("REFUGIO")
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el rol REFUGIO"
                ));
        Usuario responsable = usuarioRepository.saveAndFlush(Usuario.builder()
                .nombre(request.responsableNombre().trim())
                .apellidoPaterno(request.responsableApellidoPaterno().trim())
                .apellidoMaterno(limpiarTextoOpcional(
                        request.responsableApellidoMaterno()
                ))
                .correo(correoResponsable)
                .password(passwordEncoder.encode(request.responsablePassword()))
                .telefono(request.responsableTelefono().trim())
                .activo(true)
                .fechaRegistro(LocalDateTime.now())
                .rol(rolRefugio)
                .build());

        Refugio refugio = refugioRepository.saveAndFlush(Refugio.builder()
                .nombre(request.nombre().trim())
                .descripcion(request.descripcion().trim())
                .direccion(request.direccion().trim())
                .telefono(request.telefono().trim())
                .correo(correoRefugio)
                .activo(true)
                .aprobado(true)
                .fechaAprobacion(LocalDateTime.now())
                .aprobadoPor(administrador)
                .usuario(responsable)
                .build());

        auditoriaService.registrarAccionRefugio(
                administrador,
                refugio,
                TipoAccionAuditoria.CREAR_REFUGIO,
                request.motivo().trim(),
                Map.of(),
                estadoCompleto(refugio),
                Map.of("responsableId", responsable.getId().toString())
        );
        return refugioMapper.toAdminDetalle(refugio);
    }

    @Override
    public RefugioAdminDetalleResponse obtener(
            Long id,
            String correoAdministrador
    )
    {
        validarAdministradorActivo(correoAdministrador);
        return refugioMapper.toAdminDetalle(
                refugioRepository.findDetalleAdministrativoById(id)
                        .orElseThrow(() -> refugioNoEncontrado(id))
        );
    }

    @Override
    @Transactional
    public RefugioAdminDetalleResponse cambiarResponsable(
            Long id,
            Long responsableId,
            String motivo,
            String correoAdministrador
    )
    {
        Usuario administrador = bloquearAdministradorActivo(
                correoAdministrador
        );
        Refugio refugio = bloquearRefugio(id);
        Usuario actual = refugio.getUsuario();

        if (actual != null && actual.getId().equals(responsableId))
        {
            return refugioMapper.toAdminDetalle(refugio);
        }

        String motivoValidado = validarMotivo(motivo);
        Usuario responsable = obtenerResponsableValido(responsableId);
        Map<String, Object> estadoAnterior = estadoCompleto(refugio);

        refugio.setUsuario(responsable);
        Refugio actualizado = refugioRepository.save(refugio);
        auditoriaService.registrarAccionRefugio(
                administrador,
                actualizado,
                TipoAccionAuditoria.CAMBIAR_RESPONSABLE,
                motivoValidado,
                estadoAnterior,
                estadoCompleto(actualizado),
                Map.of("responsableId", responsable.getId().toString())
        );

        return refugioMapper.toAdminDetalle(actualizado);
    }

    @Override
    @Transactional
    public RefugioAdminDetalleResponse cambiarAprobacion(
            Long id,
            Boolean aprobado,
            String motivo,
            String correoAdministrador
    )
    {
        Usuario administrador = bloquearAdministradorActivo(
                correoAdministrador
        );
        Refugio refugio = bloquearRefugio(id);
        boolean estadoNuevo = Boolean.TRUE.equals(aprobado);
        boolean estadoActual = Boolean.TRUE.equals(refugio.getAprobado());

        if (estadoActual == estadoNuevo)
        {
            return refugioMapper.toAdminDetalle(refugio);
        }

        String motivoValidado = validarMotivo(motivo);
        Map<String, Object> estadoAnterior = estadoCompleto(refugio);
        TipoAccionAuditoria accion;

        if (estadoNuevo)
        {
            refugio.setAprobado(true);
            refugio.setFechaAprobacion(LocalDateTime.now());
            refugio.setAprobadoPor(administrador);
            accion = TipoAccionAuditoria.APROBAR_REFUGIO;
        }
        else
        {
            refugio.setAprobado(false);
            refugio.setActivo(false);
            refugio.setFechaAprobacion(null);
            refugio.setAprobadoPor(null);
            accion = TipoAccionAuditoria.RETIRAR_APROBACION_REFUGIO;
        }

        Refugio actualizado = refugioRepository.save(refugio);
        auditoriaService.registrarAccionRefugio(
                administrador,
                actualizado,
                accion,
                motivoValidado,
                estadoAnterior,
                estadoCompleto(actualizado),
                Map.of()
        );

        return refugioMapper.toAdminDetalle(actualizado);
    }

    @Override
    @Transactional
    public RefugioAdminDetalleResponse cambiarEstado(
            Long id,
            Boolean activo,
            String motivo,
            String correoAdministrador
    )
    {
        Usuario administrador = bloquearAdministradorActivo(
                correoAdministrador
        );
        Refugio refugio = bloquearRefugio(id);
        boolean estadoNuevo = Boolean.TRUE.equals(activo);
        boolean estadoActual = Boolean.TRUE.equals(refugio.getActivo());

        if (estadoActual == estadoNuevo)
        {
            return refugioMapper.toAdminDetalle(refugio);
        }

        String motivoValidado = validarMotivo(motivo);
        if (estadoNuevo)
        {
            validarActivacion(refugio);
        }

        Map<String, Object> estadoAnterior = estadoCompleto(refugio);
        refugio.setActivo(estadoNuevo);
        Refugio actualizado = refugioRepository.save(refugio);
        auditoriaService.registrarAccionRefugio(
                administrador,
                actualizado,
                estadoNuevo
                        ? TipoAccionAuditoria.ACTIVAR_REFUGIO
                        : TipoAccionAuditoria.DESACTIVAR_REFUGIO,
                motivoValidado,
                estadoAnterior,
                estadoCompleto(actualizado),
                Map.of()
        );

        return refugioMapper.toAdminDetalle(actualizado);
    }

    private void validarAdministradorActivo(String correoAdministrador)
    {
        Usuario administrador = usuarioRepository
                .findByCorreoAndActivoTrue(normalizarCorreo(
                        correoAdministrador
                ))
                .orElseThrow(this::accesoAdministrativoDenegado);
        validarRolAdministrador(administrador);
    }

    private Usuario bloquearAdministradorActivo(String correoAdministrador)
    {
        Usuario administrador = usuarioRepository
                .findActivoPorCorreoParaActualizar(normalizarCorreo(
                        correoAdministrador
                ))
                .orElseThrow(this::accesoAdministrativoDenegado);
        validarRolAdministrador(administrador);
        return administrador;
    }

    private void validarRolAdministrador(Usuario administrador)
    {
        if (!"ADMIN".equals(administrador.getRol().getNombre()))
        {
            throw accesoAdministrativoDenegado();
        }
    }

    private Usuario obtenerResponsableValido(Long responsableId)
    {
        if (responsableId == null || responsableId < 1)
        {
            throw new ParametroInvalidoException(
                    "responsableId debe ser un ID positivo"
            );
        }

        Usuario responsable = usuarioRepository
                .findByIdParaActualizar(responsableId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el responsable solicitado"
                ));

        if (!Boolean.TRUE.equals(responsable.getActivo()))
        {
            throw new ConflictoAdministrativoException(
                    "El responsable debe estar activo"
            );
        }
        if (!"REFUGIO".equals(responsable.getRol().getNombre()))
        {
            throw new ConflictoAdministrativoException(
                    "El responsable debe tener rol REFUGIO"
            );
        }
        return responsable;
    }

    private void validarActivacion(Refugio refugio)
    {
        if (!Boolean.TRUE.equals(refugio.getAprobado()))
        {
            throw new ConflictoAdministrativoException(
                    "No se puede activar un refugio pendiente de aprobación"
            );
        }

        Usuario responsable = refugio.getUsuario();
        if (responsable == null
                || !Boolean.TRUE.equals(responsable.getActivo())
                || !"REFUGIO".equals(responsable.getRol().getNombre()))
        {
            throw new ConflictoAdministrativoException(
                    "El refugio requiere un responsable activo con rol REFUGIO"
            );
        }
    }

    private Refugio bloquearRefugio(Long id)
    {
        return refugioRepository.findByIdParaActualizar(id)
                .orElseThrow(() -> refugioNoEncontrado(id));
    }

    private Specification<Refugio> crearFiltros(
            String texto,
            Boolean aprobado,
            Boolean activo,
            Long responsableId
    )
    {
        Specification<Refugio> filtros = (
                root,
                query,
                builder
        ) -> builder.conjunction();

        if (texto != null && !texto.isBlank())
        {
            String patron = "%" + escaparLike(
                    texto.trim().toLowerCase(Locale.ROOT)
            ) + "%";
            filtros = filtros.and((root, query, builder) -> builder.or(
                    builder.like(
                            builder.lower(root.get("nombre")),
                            patron,
                            '\\'
                    ),
                    builder.like(
                            builder.lower(root.get("correo")),
                            patron,
                            '\\'
                    )
            ));
        }
        if (aprobado != null)
        {
            filtros = filtros.and((root, query, builder) ->
                    builder.equal(root.get("aprobado"), aprobado));
        }
        if (activo != null)
        {
            filtros = filtros.and((root, query, builder) ->
                    builder.equal(root.get("activo"), activo));
        }
        if (responsableId != null)
        {
            filtros = filtros.and((root, query, builder) ->
                    builder.equal(
                            root.get("usuario").get("id"),
                            responsableId
                    ));
        }
        return filtros;
    }

    private Sort crearOrden(String sort)
    {
        if (sort == null)
        {
            return Sort.by(Sort.Order.desc("id"));
        }

        String[] partes = sort.split(",", -1);
        if (partes.length != 2)
        {
            throw new ParametroInvalidoException(
                    "sort debe tener el formato campo,dirección"
            );
        }
        String campo = partes[0].trim();
        String direccion = partes[1].trim();
        if (!CAMPOS_ORDEN_PERMITIDOS.contains(campo))
        {
            throw new ParametroInvalidoException(
                    "El campo de ordenamiento no está permitido"
            );
        }
        if (!"asc".equals(direccion) && !"desc".equals(direccion))
        {
            throw new ParametroInvalidoException(
                    "La dirección de ordenamiento debe ser asc o desc"
            );
        }

        Sort.Direction direction = "asc".equals(direccion)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort orden = Sort.by(direction, campo);
        return "id".equals(campo)
                ? orden
                : orden.and(Sort.by(direction, "id"));
    }

    private Boolean validarBooleano(String valor, String parametro)
    {
        if (valor == null)
        {
            return null;
        }
        if ("true".equals(valor))
        {
            return true;
        }
        if ("false".equals(valor))
        {
            return false;
        }
        throw new ParametroInvalidoException(
                parametro + " debe ser true o false"
        );
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
                    "size debe estar entre 1 y " + TAMANO_MAXIMO_PAGINA
            );
        }
    }

    private void validarResponsableId(Long responsableId)
    {
        if (responsableId != null && responsableId < 1)
        {
            throw new ParametroInvalidoException(
                    "responsableId debe ser un ID positivo"
            );
        }
    }

    private String validarMotivo(String motivo)
    {
        if (motivo == null || motivo.isBlank())
        {
            throw new ParametroInvalidoException(
                    "El motivo es obligatorio para realizar el cambio"
            );
        }
        String normalizado = motivo.trim();
        if (normalizado.length() > 500)
        {
            throw new ParametroInvalidoException(
                    "El motivo no puede superar los 500 caracteres"
            );
        }
        return normalizado;
    }

    private Map<String, Object> estadoCompleto(Refugio refugio)
    {
        if (refugio.getUsuario() != null)
        {
            return Map.of(
                    "aprobado", Boolean.TRUE.equals(refugio.getAprobado()),
                    "activo", Boolean.TRUE.equals(refugio.getActivo()),
                    "responsableId", refugio.getUsuario().getId().toString()
            );
        }
        return Map.of(
                "aprobado", Boolean.TRUE.equals(refugio.getAprobado()),
                "activo", Boolean.TRUE.equals(refugio.getActivo())
        );
    }

    private String escaparLike(String texto)
    {
        return texto
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    private String normalizarTexto(String texto)
    {
        return texto.trim().replaceAll("\\s+", " ");
    }

    private String normalizarCorreo(String correo)
    {
        if (correo == null)
        {
            throw accesoAdministrativoDenegado();
        }
        return correo.trim().toLowerCase(Locale.ROOT);
    }

    private String limpiarTextoOpcional(String texto)
    {
        if (texto == null || texto.isBlank())
        {
            return null;
        }
        return texto.trim();
    }

    private AccessDeniedException accesoAdministrativoDenegado()
    {
        return new AccessDeniedException(
                "El usuario no puede administrar refugios"
        );
    }

    private RecursoNoEncontradoException refugioNoEncontrado(Long id)
    {
        return new RecursoNoEncontradoException(
                "No se encontró el refugio con ID " + id
        );
    }
}
