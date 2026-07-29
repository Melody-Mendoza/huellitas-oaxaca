package com.huellitasoaxaca.backend.services.impl;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.response.MascotaCatalogoResponse;
import com.huellitasoaxaca.backend.dto.response.MascotaResponse;
import com.huellitasoaxaca.backend.entity.Mascota;
import com.huellitasoaxaca.backend.entity.enums.Especie;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;
import com.huellitasoaxaca.backend.entity.enums.SexoMascota;
import com.huellitasoaxaca.backend.entity.enums.TamanoMascota;
import com.huellitasoaxaca.backend.exception.ParametroInvalidoException;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.MascotaMapper;
import com.huellitasoaxaca.backend.repository.MascotaRepository;
import com.huellitasoaxaca.backend.services.MascotaService;

import lombok.RequiredArgsConstructor;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MascotaServiceImpl implements MascotaService
{
    private static final int TAMANO_MAXIMO_PAGINA = 50;
    private static final Set<String> CAMPOS_ORDEN_PERMITIDOS = Set.of(
            "id",
            "nombre",
            "edad",
            "fechaIngreso"
    );

    private final MascotaRepository mascotaRepository;
    private final MascotaMapper mascotaMapper;

    @Override
    public Page<MascotaCatalogoResponse> listarCatalogo(
            String nombre,
            Especie especie,
            SexoMascota sexo,
            TamanoMascota tamano,
            Integer edad,
            Long refugioId,
            int page,
            int size,
            String sort
    )
    {
        validarPaginacion(nombre, edad, refugioId, page, size);
        Sort orden = crearOrden(sort);

        return mascotaRepository.findAll(
                        crearFiltros(
                                nombre,
                                especie,
                                sexo,
                                tamano,
                                edad,
                                refugioId
                        ),
                        PageRequest.of(page, size, orden)
                )
                .map(mascotaMapper::toCatalogoResponse);
    }

    @Override
    public List<MascotaResponse> listarTodas() 
    {
        return convertirLista(mascotaRepository.findAll());
    }

    @Override
    public MascotaResponse obtenerPorId(Long id) 
    {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró la mascota con ID " + id
                        )
                );

        return mascotaMapper.toResponse(mascota);
    }

    @Override
    public List<MascotaResponse> buscarPorNombre(String nombre) 
    {
        return convertirLista(
                mascotaRepository.findByNombreContainingIgnoreCase(nombre)
        );
    }

    @Override
    public List<MascotaResponse> listarPorRefugio(Long refugioId) 
    {
        return convertirLista(
                mascotaRepository.findByRefugioId(refugioId)
        );
    }

    @Override
    public List<MascotaResponse> listarPorEspecie(Especie especie) 
    {
        return convertirLista(
                mascotaRepository.findByEspecie(especie)
        );
    }

    @Override
    public List<MascotaResponse> listarPorEstado(EstadoMascota estado) 
    {
        return convertirLista(
                mascotaRepository.findByEstado(estado)
        );
    }

    @Override
    public List<MascotaResponse> listarPorEspecieYEstado(
            Especie especie,
            EstadoMascota estado
    ) 
    {
        return convertirLista(
                mascotaRepository.findByEspecieAndEstado(
                        especie,
                        estado
                )
        );
    }

    private List<MascotaResponse> convertirLista(
            List<Mascota> mascotas
    ) 
    {
        return mascotas.stream()
                .map(mascotaMapper::toResponse)
                .toList();
    }

    private Specification<Mascota> crearFiltros(
            String nombre,
            Especie especie,
            SexoMascota sexo,
            TamanoMascota tamano,
            Integer edad,
            Long refugioId
    )
    {
        return (root, query, criteriaBuilder) ->
        {
            var refugio = root.join("refugio", JoinType.INNER);
            List<Predicate> filtros = new ArrayList<>();

            filtros.add(criteriaBuilder.equal(
                    root.get("estado"),
                    EstadoMascota.DISPONIBLE
            ));
            filtros.add(criteriaBuilder.isTrue(refugio.get("activo")));

            if (nombre != null && !nombre.isBlank())
            {
                String patron = "%"
                        + escaparLike(nombre.trim().toLowerCase(Locale.ROOT))
                        + "%";
                filtros.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nombre")),
                        patron,
                        '\\'
                ));
            }
            if (especie != null)
            {
                filtros.add(criteriaBuilder.equal(root.get("especie"), especie));
            }
            if (sexo != null)
            {
                filtros.add(criteriaBuilder.equal(root.get("sexo"), sexo));
            }
            if (tamano != null)
            {
                filtros.add(criteriaBuilder.equal(root.get("tamano"), tamano));
            }
            if (edad != null)
            {
                filtros.add(criteriaBuilder.equal(root.get("edad"), edad));
            }
            if (refugioId != null)
            {
                filtros.add(criteriaBuilder.equal(refugio.get("id"), refugioId));
            }

            return criteriaBuilder.and(filtros.toArray(Predicate[]::new));
        };
    }

    private void validarPaginacion(
            String nombre,
            Integer edad,
            Long refugioId,
            int page,
            int size
    )
    {
        if (page < 0)
        {
            throw new ParametroInvalidoException("page no puede ser negativo");
        }
        if (size < 1 || size > TAMANO_MAXIMO_PAGINA)
        {
            throw new ParametroInvalidoException(
                    "size debe estar entre 1 y " + TAMANO_MAXIMO_PAGINA
            );
        }
        if (nombre != null && nombre.trim().length() > 100)
        {
            throw new ParametroInvalidoException(
                    "nombre no puede superar los 100 caracteres"
            );
        }
        if (edad != null && (edad < 0 || edad > 40))
        {
            throw new ParametroInvalidoException(
                    "edad debe estar entre 0 y 40"
            );
        }
        if (refugioId != null && refugioId < 1)
        {
            throw new ParametroInvalidoException(
                    "refugioId debe ser mayor que cero"
            );
        }
    }

    private Sort crearOrden(String sort)
    {
        String[] partes = sort.split(",", -1);
        if (partes.length != 2)
        {
            throw new ParametroInvalidoException(
                    "sort debe tener el formato campo,direccion"
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
        if (!"asc".equalsIgnoreCase(direccion)
                && !"desc".equalsIgnoreCase(direccion))
        {
            throw new ParametroInvalidoException(
                    "La dirección de orden debe ser asc o desc"
            );
        }

        return Sort.by(Sort.Direction.fromString(direccion), campo);
    }

    private String escaparLike(String valor)
    {
        return valor
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
