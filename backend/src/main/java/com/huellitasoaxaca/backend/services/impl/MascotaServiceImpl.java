package com.huellitasoaxaca.backend.services.impl;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.response.MascotaCatalogoResponse;
import com.huellitasoaxaca.backend.dto.response.MascotaDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.MascotaResponse;
import com.huellitasoaxaca.backend.dto.request.MascotaCrearRequest;
import com.huellitasoaxaca.backend.dto.request.MascotaActualizarRequest;
import com.huellitasoaxaca.backend.dto.request.MascotaEstadoActualizarRequest;
import com.huellitasoaxaca.backend.dto.response.MascotaPropiaDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.MascotaPropiaResumenResponse;
import com.huellitasoaxaca.backend.entity.ImagenMascota;
import com.huellitasoaxaca.backend.entity.Mascota;
import com.huellitasoaxaca.backend.entity.Refugio;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.entity.enums.Especie;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;
import com.huellitasoaxaca.backend.entity.enums.SexoMascota;
import com.huellitasoaxaca.backend.entity.enums.TamanoMascota;
import com.huellitasoaxaca.backend.exception.ParametroInvalidoException;
import com.huellitasoaxaca.backend.exception.ReglaNegocioException;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.MascotaMapper;
import com.huellitasoaxaca.backend.repository.MascotaRepository;
import com.huellitasoaxaca.backend.repository.ImagenMascotaRepository;
import com.huellitasoaxaca.backend.repository.RefugioRepository;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
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
    private final ImagenMascotaRepository imagenMascotaRepository;
    private final RefugioRepository refugioRepository;
    private final UsuarioRepository usuarioRepository;
    private final MascotaMapper mascotaMapper;

    @Override
    @Transactional
    public MascotaPropiaDetalleResponse crearPropia(
            Long refugioId,
            MascotaCrearRequest request,
            String correoAutenticado
    )
    {
        Usuario usuario = obtenerResponsableActivo(correoAutenticado);
        Refugio refugio = obtenerRefugioPropioActivo(
                refugioId,
                usuario.getId()
        );
        Mascota mascota = Mascota.builder()
                .nombre(normalizarTextoObligatorio(request.nombre()))
                .especie(request.especie())
                .raza(normalizarTextoObligatorio(request.raza()))
                .sexo(request.sexo())
                .edad(request.edad())
                .peso(request.peso())
                .tamano(request.tamano())
                .descripcion(request.descripcion().trim())
                .estado(EstadoMascota.DISPONIBLE)
                .fechaIngreso(LocalDate.now())
                .imagen(null)
                .refugio(refugio)
                .build();

        Mascota guardada = mascotaRepository.save(mascota);

        return mascotaMapper.toPropiaDetalle(guardada, List.of());
    }

    @Override
    public Page<MascotaPropiaResumenResponse> listarPropias(
            Long refugioId,
            String nombre,
            Especie especie,
            EstadoMascota estado,
            int page,
            int size,
            String correoAutenticado
    )
    {
        validarPaginacionPropia(nombre, page, size);
        Usuario usuario = obtenerResponsableActivo(correoAutenticado);
        obtenerRefugioPropioActivo(refugioId, usuario.getId());
        Sort orden = Sort.by(
                Sort.Order.desc("fechaIngreso"),
                Sort.Order.desc("id")
        );

        return mascotaRepository.findAll(
                        crearFiltrosPropios(
                                refugioId,
                                nombre,
                                especie,
                                estado
                        ),
                        PageRequest.of(page, size, orden)
                )
                .map(mascotaMapper::toPropiaResumen);
    }

    @Override
    public MascotaPropiaDetalleResponse obtenerDetallePropio(
            Long refugioId,
            Long mascotaId,
            String correoAutenticado
    )
    {
        Usuario usuario = obtenerResponsableActivo(correoAutenticado);
        obtenerRefugioPropioActivo(refugioId, usuario.getId());
        Mascota mascota = mascotaRepository
                .findByIdAndRefugioId(mascotaId, refugioId)
                .orElseThrow(this::mascotaPropiaNoEncontrada);
        List<String> imagenes = imagenMascotaRepository
                .findByMascotaIdOrderByIdAsc(mascotaId)
                .stream()
                .filter(imagen -> !Boolean.TRUE.equals(imagen.getPrincipal()))
                .map(ImagenMascota::getUrl)
                .toList();

        return mascotaMapper.toPropiaDetalle(mascota, imagenes);
    }

    @Override
    @Transactional
    public MascotaPropiaDetalleResponse actualizarPropia(
            Long refugioId,
            Long mascotaId,
            MascotaActualizarRequest request,
            String correoAutenticado
    )
    {
        Mascota mascota = obtenerMascotaPropiaParaActualizar(
                refugioId,
                mascotaId,
                correoAutenticado
        );

        if (request.nombre() != null)
        {
            mascota.setNombre(normalizarTextoObligatorio(request.nombre()));
        }
        if (request.especie() != null)
        {
            mascota.setEspecie(request.especie());
        }
        if (request.raza() != null)
        {
            mascota.setRaza(normalizarTextoObligatorio(request.raza()));
        }
        if (request.sexo() != null)
        {
            mascota.setSexo(request.sexo());
        }
        if (request.edad() != null)
        {
            mascota.setEdad(request.edad());
        }
        if (request.peso() != null)
        {
            mascota.setPeso(request.peso());
        }
        if (request.tamano() != null)
        {
            mascota.setTamano(request.tamano());
        }
        if (request.descripcion() != null)
        {
            mascota.setDescripcion(request.descripcion().trim());
        }

        return toDetallePropio(mascotaRepository.save(mascota));
    }

    @Override
    @Transactional
    public MascotaPropiaDetalleResponse actualizarEstadoPropio(
            Long refugioId,
            Long mascotaId,
            MascotaEstadoActualizarRequest request,
            String correoAutenticado
    )
    {
        Mascota mascota = obtenerMascotaPropiaParaActualizar(
                refugioId,
                mascotaId,
                correoAutenticado
        );
        EstadoMascota actual = mascota.getEstado();
        EstadoMascota nuevo = request.estado();

        if (actual != nuevo && !transicionPermitida(actual, nuevo))
        {
            throw new ReglaNegocioException(
                    "La transición de estado solicitada no está permitida"
            );
        }

        mascota.setEstado(nuevo);

        return toDetallePropio(mascotaRepository.save(mascota));
    }

    @Override
    public MascotaDetalleResponse obtenerDetallePublico(Long id)
    {
        Mascota mascota = mascotaRepository
                .findByIdAndEstadoAndRefugioActivoTrueAndRefugioAprobadoTrue(
                        id,
                        EstadoMascota.DISPONIBLE
                )
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró la mascota solicitada"
                        )
                );

        List<String> imagenesAdicionales = imagenMascotaRepository
                .findByMascotaIdOrderByIdAsc(id)
                .stream()
                .filter(imagen -> !Boolean.TRUE.equals(imagen.getPrincipal()))
                .map(ImagenMascota::getUrl)
                .toList();

        return mascotaMapper.toDetalleResponse(
                mascota,
                imagenesAdicionales
        );
    }

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
            filtros.add(criteriaBuilder.isTrue(refugio.get("aprobado")));

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

    private Specification<Mascota> crearFiltrosPropios(
            Long refugioId,
            String nombre,
            Especie especie,
            EstadoMascota estado
    )
    {
        return (root, query, criteriaBuilder) ->
        {
            List<Predicate> filtros = new ArrayList<>();
            filtros.add(criteriaBuilder.equal(
                    root.get("refugio").get("id"),
                    refugioId
            ));

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
            if (estado != null)
            {
                filtros.add(criteriaBuilder.equal(root.get("estado"), estado));
            }

            return criteriaBuilder.and(filtros.toArray(Predicate[]::new));
        };
    }

    private Usuario obtenerResponsableActivo(String correoAutenticado)
    {
        Usuario usuario = usuarioRepository
                .findByCorreoAndActivoTrue(
                        correoAutenticado.trim().toLowerCase(Locale.ROOT)
                )
                .orElseThrow(() -> new AccessDeniedException(
                        "El usuario no puede administrar mascotas"
                ));

        if (!"REFUGIO".equals(usuario.getRol().getNombre()))
        {
            throw new AccessDeniedException(
                    "El rol no puede administrar mascotas"
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

        if (!Boolean.TRUE.equals(refugio.getActivo())
                || !Boolean.TRUE.equals(refugio.getAprobado()))
        {
            throw new AccessDeniedException(
                    "El refugio no está autorizado para operar"
            );
        }

        return refugio;
    }

    private void validarPaginacionPropia(String nombre, int page, int size)
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
    }

    private String normalizarTextoObligatorio(String valor)
    {
        return valor.trim().replaceAll("\\s+", " ");
    }

    private RecursoNoEncontradoException mascotaPropiaNoEncontrada()
    {
        return new RecursoNoEncontradoException(
                "No se encontró la mascota solicitada"
        );
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

    private MascotaPropiaDetalleResponse toDetallePropio(Mascota mascota)
    {
        List<String> imagenes = imagenMascotaRepository
                .findByMascotaIdOrderByIdAsc(mascota.getId())
                .stream()
                .filter(imagen -> !Boolean.TRUE.equals(imagen.getPrincipal()))
                .map(ImagenMascota::getUrl)
                .toList();

        return mascotaMapper.toPropiaDetalle(mascota, imagenes);
    }

    private boolean transicionPermitida(
            EstadoMascota actual,
            EstadoMascota nuevo
    )
    {
        return (actual == EstadoMascota.DISPONIBLE
                        && nuevo == EstadoMascota.EN_PROCESO)
                || (actual == EstadoMascota.EN_PROCESO
                        && nuevo == EstadoMascota.DISPONIBLE);
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
