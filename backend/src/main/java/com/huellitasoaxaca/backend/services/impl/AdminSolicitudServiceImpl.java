package com.huellitasoaxaca.backend.services.impl;

import java.util.Locale;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.response.PaginaResponse;
import com.huellitasoaxaca.backend.dto.response.SolicitudAdminDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.SolicitudAdminResumenResponse;
import com.huellitasoaxaca.backend.entity.SolicitudAdopcion;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;
import com.huellitasoaxaca.backend.exception.ParametroInvalidoException;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.AdminSolicitudMapper;
import com.huellitasoaxaca.backend.repository.SolicitudAdopcionRepository;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
import com.huellitasoaxaca.backend.services.AdminSolicitudService;

import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminSolicitudServiceImpl implements AdminSolicitudService
{
    private static final int TAMANO_MAXIMO_PAGINA = 50;
    private static final Set<String> CAMPOS_ORDEN_PERMITIDOS = Set.of(
            "id",
            "fechaSolicitud",
            "estado"
    );

    private final SolicitudAdopcionRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final AdminSolicitudMapper adminSolicitudMapper;

    @Override
    public PaginaResponse<SolicitudAdminResumenResponse> listar(
            int page,
            int size,
            String sort,
            String texto,
            String estado,
            Long mascotaId,
            Long refugioId,
            String correoAdministrador
    )
    {
        validarAdministradorActivo(correoAdministrador);
        validarPaginacion(page, size);

        Page<SolicitudAdminResumenResponse> solicitudes = solicitudRepository
                .findAll(
                        crearFiltros(
                                texto,
                                validarEstado(estado),
                                mascotaId,
                                refugioId
                        ),
                        PageRequest.of(page, size, crearOrden(sort))
                )
                .map(adminSolicitudMapper::toAdminResumen);

        return PaginaResponse.desde(solicitudes);
    }

    @Override
    public SolicitudAdminDetalleResponse obtener(
            Long id,
            String correoAdministrador
    )
    {
        validarAdministradorActivo(correoAdministrador);
        return adminSolicitudMapper.toAdminDetalle(
                solicitudRepository.findAdminDetalleById(id)
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "No se encontró la solicitud con ID " + id
                        ))
        );
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

    private void validarRolAdministrador(Usuario administrador)
    {
        if (!"ADMIN".equals(administrador.getRol().getNombre()))
        {
            throw accesoAdministrativoDenegado();
        }
    }

    private AccessDeniedException accesoAdministrativoDenegado()
    {
        return new AccessDeniedException(
                "El usuario no puede administrar solicitudes"
        );
    }

    private Specification<SolicitudAdopcion> crearFiltros(
            String texto,
            EstadoSolicitud estado,
            Long mascotaId,
            Long refugioId
    )
    {
        return (root, query, builder) ->
        {
            var predicate = builder.conjunction();

            if (texto != null && !texto.isBlank())
            {
                String patron = "%"
                        + escaparLike(texto.trim().toLowerCase(Locale.ROOT))
                        + "%";
                predicate = builder.and(predicate, builder.or(
                        builder.like(
                                builder.lower(root.get("comentarios")),
                                patron,
                                '\\'
                        )
                ));
            }
            if (estado != null)
            {
                predicate = builder.and(predicate,
                        builder.equal(root.get("estado"), estado));
            }
            if (mascotaId != null)
            {
                predicate = builder.and(predicate,
                        builder.equal(
                                root.get("mascota").get("id"),
                                mascotaId
                        ));
            }
            if (refugioId != null)
            {
                var mascota = root.join("mascota", JoinType.INNER);
                predicate = builder.and(predicate,
                        builder.equal(
                                mascota.get("refugio").get("id"),
                                refugioId
                        ));
            }

            return predicate;
        };
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

    private EstadoSolicitud validarEstado(String valor)
    {
        if (valor == null)
        {
            return null;
        }
        try
        {
            return EstadoSolicitud.valueOf(valor.toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException exception)
        {
            throw new ParametroInvalidoException(
                    "estado debe ser PENDIENTE, APROBADA o RECHAZADA"
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
                    "size debe estar entre 1 y " + TAMANO_MAXIMO_PAGINA
            );
        }
    }

    private String escaparLike(String texto)
    {
        return texto
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    private String normalizarCorreo(String correo)
    {
        if (correo == null)
        {
            throw accesoAdministrativoDenegado();
        }
        return correo.trim().toLowerCase(Locale.ROOT);
    }
}
