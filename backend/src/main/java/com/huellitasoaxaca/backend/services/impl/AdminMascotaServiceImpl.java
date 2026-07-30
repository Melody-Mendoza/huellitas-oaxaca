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

import com.huellitasoaxaca.backend.dto.response.MascotaAdminDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.MascotaAdminResumenResponse;
import com.huellitasoaxaca.backend.dto.response.PaginaResponse;
import com.huellitasoaxaca.backend.entity.Mascota;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.entity.enums.Especie;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;
import com.huellitasoaxaca.backend.entity.enums.SexoMascota;
import com.huellitasoaxaca.backend.entity.enums.TamanoMascota;
import com.huellitasoaxaca.backend.exception.ParametroInvalidoException;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.AdminMascotaMapper;
import com.huellitasoaxaca.backend.repository.MascotaRepository;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
import com.huellitasoaxaca.backend.services.AdminMascotaService;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMascotaServiceImpl implements AdminMascotaService
{
    private static final int TAMANO_MAXIMO_PAGINA = 50;
    private static final Set<String> CAMPOS_ORDEN_PERMITIDOS = Set.of(
            "id",
            "nombre",
            "edad",
            "fechaIngreso",
            "especie",
            "estado"
    );

    private final MascotaRepository mascotaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AdminMascotaMapper adminMascotaMapper;

    @Override
    public PaginaResponse<MascotaAdminResumenResponse> listar(
            int page,
            int size,
            String sort,
            String texto,
            String especie,
            String sexo,
            String tamano,
            String estado,
            Long refugioId,
            String correoAdministrador
    )
    {
        validarAdministradorActivo(correoAdministrador);
        validarPaginacion(page, size);

        Page<MascotaAdminResumenResponse> mascotas = mascotaRepository
                .findAll(
                        crearFiltros(
                                texto,
                                validarEspecie(especie),
                                validarSexo(sexo),
                                validarTamano(tamano),
                                validarEstado(estado),
                                refugioId
                        ),
                        PageRequest.of(page, size, crearOrden(sort))
                )
                .map(adminMascotaMapper::toAdminResumen);

        return PaginaResponse.desde(mascotas);
    }

    @Override
    public MascotaAdminDetalleResponse obtener(
            Long id,
            String correoAdministrador
    )
    {
        validarAdministradorActivo(correoAdministrador);
        return adminMascotaMapper.toAdminDetalle(
                mascotaRepository.findAdminDetalleById(id)
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "No se encontró la mascota con ID " + id
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
                "El usuario no puede administrar mascotas"
        );
    }

    private Specification<Mascota> crearFiltros(
            String texto,
            Especie especie,
            SexoMascota sexo,
            TamanoMascota tamano,
            EstadoMascota estado,
            Long refugioId
    )
    {
        return (root, query, builder) ->
        {
            var refugio = root.join("refugio", JoinType.LEFT);
            var predicate = builder.conjunction();

            if (texto != null && !texto.isBlank())
            {
                String patron = "%"
                        + escaparLike(texto.trim().toLowerCase(Locale.ROOT))
                        + "%";
                predicate = builder.and(predicate, builder.or(
                        builder.like(
                                builder.lower(root.get("nombre")),
                                patron,
                                '\\'
                        ),
                        builder.like(
                                builder.lower(root.get("raza")),
                                patron,
                                '\\'
                        )
                ));
            }
            if (especie != null)
            {
                predicate = builder.and(predicate,
                        builder.equal(root.get("especie"), especie));
            }
            if (sexo != null)
            {
                predicate = builder.and(predicate,
                        builder.equal(root.get("sexo"), sexo));
            }
            if (tamano != null)
            {
                predicate = builder.and(predicate,
                        builder.equal(root.get("tamano"), tamano));
            }
            if (estado != null)
            {
                predicate = builder.and(predicate,
                        builder.equal(root.get("estado"), estado));
            }
            if (refugioId != null)
            {
                predicate = builder.and(predicate,
                        builder.equal(refugio.get("id"), refugioId));
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

    private Especie validarEspecie(String valor)
    {
        if (valor == null)
        {
            return null;
        }
        try
        {
            return Especie.valueOf(valor.toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException exception)
        {
            throw new ParametroInvalidoException(
                    "especie debe ser PERRO o GATO"
            );
        }
    }

    private SexoMascota validarSexo(String valor)
    {
        if (valor == null)
        {
            return null;
        }
        try
        {
            return SexoMascota.valueOf(valor.toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException exception)
        {
            throw new ParametroInvalidoException(
                    "sexo debe ser MACHO o HEMBRA"
            );
        }
    }

    private TamanoMascota validarTamano(String valor)
    {
        if (valor == null)
        {
            return null;
        }
        try
        {
            return TamanoMascota.valueOf(valor.toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException exception)
        {
            throw new ParametroInvalidoException(
                    "tamano debe ser PEQUENO, MEDIANO o GRANDE"
            );
        }
    }

    private EstadoMascota validarEstado(String valor)
    {
        if (valor == null)
        {
            return null;
        }
        try
        {
            return EstadoMascota.valueOf(valor.toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException exception)
        {
            throw new ParametroInvalidoException(
                    "estado debe ser DISPONIBLE, ADOPTADO o EN_PROCESO"
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
