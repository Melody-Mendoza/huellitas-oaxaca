package com.huellitasoaxaca.backend.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.response.AuditoriaAdminDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.AuditoriaAdminResumenResponse;
import com.huellitasoaxaca.backend.dto.response.PaginaResponse;
import com.huellitasoaxaca.backend.entity.AuditoriaAdministrativa;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.entity.enums.TipoAccionAuditoria;
import com.huellitasoaxaca.backend.entity.enums.TipoRecursoAuditoria;
import com.huellitasoaxaca.backend.exception.ParametroInvalidoException;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.AdminAuditoriaMapper;
import com.huellitasoaxaca.backend.repository.AuditoriaAdministrativaRepository;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
import com.huellitasoaxaca.backend.services.AdminAuditoriaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAuditoriaServiceImpl implements AdminAuditoriaService
{
    private static final int TAMANO_MAXIMO_PAGINA = 50;
    private static final Set<String> CAMPOS_ORDEN_PERMITIDOS = Set.of(
            "id",
            "fecha",
            "tipoAccion",
            "tipoRecurso",
            "resultado"
    );

    private final AuditoriaAdministrativaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AdminAuditoriaMapper adminAuditoriaMapper;

    @Override
    public PaginaResponse<AuditoriaAdminResumenResponse> listar(
            int page,
            int size,
            String sort,
            String tipoAccion,
            String tipoRecurso,
            Long administradorId,
            String desde,
            String hasta,
            String correoAdministrador
    )
    {
        validarAdministradorActivo(correoAdministrador);
        validarPaginacion(page, size);

        Page<AuditoriaAdminResumenResponse> auditorias = auditoriaRepository
                .findAll(
                        crearFiltros(
                                tipoAccion,
                                tipoRecurso,
                                administradorId,
                                validarFecha(desde, "desde"),
                                validarFecha(hasta, "hasta")
                        ),
                        PageRequest.of(page, size, crearOrden(sort))
                )
                .map(adminAuditoriaMapper::toAdminResumen);

        return PaginaResponse.desde(auditorias);
    }

    @Override
    public AuditoriaAdminDetalleResponse obtener(
            Long id,
            String correoAdministrador
    )
    {
        validarAdministradorActivo(correoAdministrador);
        return adminAuditoriaMapper.toAdminDetalle(
                auditoriaRepository.findAdminDetalleById(id)
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "No se encontró el registro de auditoría con ID "
                                        + id
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
                "El usuario no puede administrar auditoría"
        );
    }

    private Specification<AuditoriaAdministrativa> crearFiltros(
            String tipoAccion,
            String tipoRecurso,
            Long administradorId,
            LocalDate desde,
            LocalDate hasta
    )
    {
        return (root, query, builder) ->
        {
            var predicate = builder.conjunction();

            if (tipoAccion != null && !tipoAccion.isBlank())
            {
                try
                {
                    TipoAccionAuditoria accion = TipoAccionAuditoria.valueOf(
                            tipoAccion.trim().toUpperCase(Locale.ROOT)
                    );
                    predicate = builder.and(predicate,
                            builder.equal(root.get("tipoAccion"), accion));
                }
                catch (IllegalArgumentException exception)
                {
                    throw new ParametroInvalidoException(
                            "tipoAccion no es válido"
                    );
                }
            }
            if (tipoRecurso != null && !tipoRecurso.isBlank())
            {
                try
                {
                    TipoRecursoAuditoria recurso = TipoRecursoAuditoria.valueOf(
                            tipoRecurso.trim().toUpperCase(Locale.ROOT)
                    );
                    predicate = builder.and(predicate,
                            builder.equal(root.get("tipoRecurso"), recurso));
                }
                catch (IllegalArgumentException exception)
                {
                    throw new ParametroInvalidoException(
                            "tipoRecurso no es válido"
                    );
                }
            }
            if (administradorId != null)
            {
                predicate = builder.and(predicate,
                        builder.equal(
                                root.get("administrador").get("id"),
                                administradorId
                        ));
            }
            if (desde != null)
            {
                predicate = builder.and(predicate,
                        builder.greaterThanOrEqualTo(
                                root.get("fecha"),
                                desde.atStartOfDay()
                        ));
            }
            if (hasta != null)
            {
                predicate = builder.and(predicate,
                        builder.lessThanOrEqualTo(
                                root.get("fecha"),
                                hasta.atTime(LocalTime.MAX)
                        ));
            }

            return predicate;
        };
    }

    private Sort crearOrden(String sort)
    {
        if (sort == null)
        {
            return Sort.by(Sort.Order.desc("fecha"), Sort.Order.desc("id"));
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

    private LocalDate validarFecha(String valor, String parametro)
    {
        if (valor == null)
        {
            return null;
        }
        try
        {
            return LocalDate.parse(
                    valor.trim(),
                    DateTimeFormatter.ISO_LOCAL_DATE
            );
        }
        catch (DateTimeParseException exception)
        {
            throw new ParametroInvalidoException(
                    parametro + " debe tener formato ISO (yyyy-MM-dd)"
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

    private String normalizarCorreo(String correo)
    {
        if (correo == null)
        {
            throw accesoAdministrativoDenegado();
        }
        return correo.trim().toLowerCase(Locale.ROOT);
    }
}
