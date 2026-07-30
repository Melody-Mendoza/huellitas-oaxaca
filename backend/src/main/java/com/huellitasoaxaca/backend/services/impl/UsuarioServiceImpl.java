package com.huellitasoaxaca.backend.services.impl;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.huellitasoaxaca.backend.dto.request.CambiarPasswordRequest;
import com.huellitasoaxaca.backend.dto.request.UsuarioActualizarRequest;
import com.huellitasoaxaca.backend.dto.request.UsuarioAdminCrearRequest;
import com.huellitasoaxaca.backend.dto.response.PaginaResponse;
import com.huellitasoaxaca.backend.dto.response.UsuarioResponse;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.exception.ConflictoAdministrativoException;
import com.huellitasoaxaca.backend.exception.ParametroInvalidoException;
import com.huellitasoaxaca.backend.exception.ReglaNegocioException;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.exception.RecursoDuplicadoException;
import com.huellitasoaxaca.backend.mapper.UsuarioMapper;
import com.huellitasoaxaca.backend.repository.RefugioRepository;
import com.huellitasoaxaca.backend.repository.RolRepository;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
import com.huellitasoaxaca.backend.services.AuditoriaAdministrativaService;
import com.huellitasoaxaca.backend.services.PerfilFotoStorageService;
import com.huellitasoaxaca.backend.services.UsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService
{
        private static final int TAMANO_MAXIMO_PAGINA = 50;
        private static final Set<String> ROLES_PERMITIDOS = Set.of(
                "USUARIO",
                "REFUGIO",
                "ADMIN"
        );
        private static final Set<String> CAMPOS_ORDEN_PERMITIDOS = Set.of(
                "id",
                "nombre",
                "apellidoPaterno",
                "apellidoMaterno",
                "correo",
                "fechaRegistro",
                "activo"
        );

        private final UsuarioRepository usuarioRepository;
        private final RolRepository rolRepository;
        private final RefugioRepository refugioRepository;
        private final UsuarioMapper usuarioMapper;
        private final PasswordEncoder passwordEncoder;
        private final PerfilFotoStorageService perfilFotoStorageService;
        private final AuditoriaAdministrativaService auditoriaService;

        @Override
        public List<UsuarioResponse> listarTodos()
        {
                return usuarioRepository.findAll()
                        .stream()
                        .map(usuarioMapper::toResponse)
                        .toList();
        }

        @Override
        public PaginaResponse<UsuarioResponse> listarAdministrativamente(
                int page,
                int size,
                String sort,
                String texto,
                String rol,
                String activo,
                String correoAdministrador
        )
        {
                validarAdministradorActivo(correoAdministrador);
                validarPaginacion(page, size);

                String rolValidado = validarRol(rol);
                Boolean activoValidado = validarActivo(activo);
                Sort orden = crearOrden(sort);
                Specification<Usuario> filtros = crearFiltros(
                        texto,
                        rolValidado,
                        activoValidado
                );

                Page<UsuarioResponse> usuarios = usuarioRepository.findAll(
                                filtros,
                                PageRequest.of(page, size, orden)
                        )
                        .map(usuarioMapper::toResponse);

                return PaginaResponse.desde(usuarios);
        }

        @Override
        public UsuarioResponse obtenerAdministrativamente(
                Long id,
                String correoAdministrador
        )
        {
                validarAdministradorActivo(correoAdministrador);
                return usuarioMapper.toResponse(buscarEntidadPorId(id));
        }

        @Override
        public UsuarioResponse obtenerPorId(Long id)
        {
                Usuario usuario = buscarEntidadPorId(id);
                return usuarioMapper.toResponse(usuario);
        }

        @Override
        public UsuarioResponse obtenerActivoPorCorreo(String correo)
        {
                Usuario usuario = usuarioRepository.findByCorreoAndActivoTrue(
                                correo.trim().toLowerCase()
                        )
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "No se encontró el usuario autenticado"
                                )
                        );

                return usuarioMapper.toResponse(usuario);
        }

        @Override
        public List<UsuarioResponse> listarActivos()
        {
                return usuarioRepository.findByActivoTrue()
                        .stream()
                        .map(usuarioMapper::toResponse)
                        .toList();
        }

        private Usuario buscarEntidadPorId(Long id)
        {
                return usuarioRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "No se encontró el usuario con ID " + id
                                )
                        );
        }

        @Override
        @Transactional
        public UsuarioResponse actualizarPerfil(
                String correoAutenticado,
                UsuarioActualizarRequest request
        )
        {
                Usuario usuario = usuarioRepository
                        .findByCorreoAndActivoTrue(
                                correoAutenticado.trim().toLowerCase()
                        )
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "No se encontró el usuario autenticado"
                                )
                        );

                usuario.setNombre(request.nombre().trim());
                usuario.setApellidoPaterno(
                        request.apellidoPaterno().trim()
                );
                usuario.setApellidoMaterno(
                        limpiarTextoOpcional(request.apellidoMaterno())
                );
                usuario.setTelefono(
                        limpiarTextoOpcional(request.telefono())
                );

                Usuario actualizado = usuarioRepository.save(usuario);

                return usuarioMapper.toResponse(actualizado);
        }

        @Override
        @Transactional
        public UsuarioResponse actualizarFotoPerfil(
                String correoAutenticado,
                MultipartFile foto
        )
        {
                Usuario usuario = buscarActivoParaActualizar(
                        correoAutenticado
                );
                String fotoAnterior = usuario.getFotoPerfil();
                String fotoNueva = perfilFotoStorageService.guardar(foto);

                registrarReemplazoTrasTransaccion(
                        fotoAnterior,
                        fotoNueva
                );

                usuario.setFotoPerfil(fotoNueva);

                return usuarioMapper.toResponse(
                        usuarioRepository.saveAndFlush(usuario)
                );
        }

        @Override
        @Transactional
        public UsuarioResponse eliminarFotoPerfil(
                String correoAutenticado
        )
        {
                Usuario usuario = buscarActivoParaActualizar(
                        correoAutenticado
                );
                String fotoAnterior = usuario.getFotoPerfil();

                if (fotoAnterior == null)
                {
                        return usuarioMapper.toResponse(usuario);
                }

                registrarEliminacionTrasCommit(fotoAnterior);
                usuario.setFotoPerfil(null);

                return usuarioMapper.toResponse(
                        usuarioRepository.saveAndFlush(usuario)
                );
        }

        @Override
        @Transactional
        public void cambiarPassword(
                String correoAutenticado,
                CambiarPasswordRequest request
        )
        {
                Usuario usuario = usuarioRepository
                        .findByCorreoAndActivoTrue(correoAutenticado.trim().toLowerCase())
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "No se encontró el usuario autenticado"
                                )
                        );

                if (usuario.getPassword() == null)
                {
                        throw new ReglaNegocioException(
                                "Esta cuenta no tiene una contraseña local"
                        );
                }

                if (!passwordEncoder.matches(request.passwordActual(),usuario.getPassword()))
                {
                        throw new ReglaNegocioException(
                                "La contraseña actual es incorrecta"
                        );
                }

                if (!request.nuevaPassword().equals(request.confirmarPassword()))
                {
                        throw new ReglaNegocioException(
                                "Las contraseñas nuevas no coinciden"
                        );
                }

                if (passwordEncoder.matches(request.nuevaPassword(),usuario.getPassword()))
                {
                        throw new ReglaNegocioException(
                                "La nueva contraseña debe ser diferente a la actual"
                        );
                }

                usuario.setPassword(passwordEncoder.encode(request.nuevaPassword()));

                usuarioRepository.save(usuario);
        }

        @Override
        @Transactional
        public UsuarioResponse cambiarEstado(
                Long id,
                Boolean activo,
                String motivo,
                String correoAdministrador
        )
        {
                bloquearRolAdministrador();
                Usuario administrador = bloquearAdministradorActivo(
                        correoAdministrador
                );
                Usuario usuario = administrador.getId().equals(id)
                        ? administrador
                        : usuarioRepository.findByIdParaActualizar(id)
                                .orElseThrow(() -> usuarioNoEncontrado(id));

                boolean estadoAnterior = Boolean.TRUE.equals(
                        usuario.getActivo()
                );
                boolean estadoNuevo = Boolean.TRUE.equals(activo);

                if (estadoAnterior == estadoNuevo)
                {
                        return usuarioMapper.toResponse(usuario);
                }

                String motivoValidado = validarMotivo(motivo);

                if (!estadoNuevo)
                {
                        if ("ADMIN".equals(usuario.getRol().getNombre())
                                && usuarioRepository
                                        .countByRolNombreAndActivoTrue("ADMIN")
                                        <= 1)
                        {
                                throw new ConflictoAdministrativoException(
                                        "Debe existir al menos un administrador activo"
                                );
                        }

                        if (usuario.getId().equals(administrador.getId()))
                        {
                                throw new ConflictoAdministrativoException(
                                        "No puedes desactivar tu propia cuenta"
                                );
                        }

                        if (refugioRepository
                                .existsByUsuarioIdAndActivoTrue(usuario.getId()))
                        {
                                throw new ConflictoAdministrativoException(
                                        "No se puede desactivar un responsable con refugios activos"
                                );
                        }
                }

                usuario.setActivo(estadoNuevo);
                Usuario actualizado = usuarioRepository.save(usuario);
                auditoriaService.registrarCambioEstadoUsuario(
                        administrador,
                        actualizado,
                        estadoAnterior,
                        estadoNuevo,
                        motivoValidado
                );

                return usuarioMapper.toResponse(actualizado);
        }

        @Override
        @Transactional
        public UsuarioResponse crearAdministrador(
                UsuarioAdminCrearRequest request,
                String correoAdministrador
        )
        {
                bloquearRolAdministrador();
                Usuario administrador = bloquearAdministradorActivo(
                        correoAdministrador
                );
                String correo = normalizarCorreo(request.correo());

                if (usuarioRepository.existsByCorreo(correo))
                {
                        throw new RecursoDuplicadoException(
                                "Ya existe un usuario con ese correo"
                        );
                }

                var rol = rolRepository.findByNombre("ADMIN")
                        .orElseThrow(this::accesoAdministrativoDenegado);
                Usuario nuevo = Usuario.builder()
                        .nombre(request.nombre().trim())
                        .apellidoPaterno(request.apellidoPaterno().trim())
                        .apellidoMaterno(limpiarTextoOpcional(
                                request.apellidoMaterno()
                        ))
                        .correo(correo)
                        .password(passwordEncoder.encode(request.password()))
                        .telefono(limpiarTextoOpcional(request.telefono()))
                        .activo(true)
                        .fechaRegistro(java.time.LocalDateTime.now())
                        .rol(rol)
                        .build();
                Usuario guardado = usuarioRepository.saveAndFlush(nuevo);
                auditoriaService.registrarCreacionUsuario(
                        administrador,
                        guardado,
                        "Alta de administrador desde el panel"
                );
                return usuarioMapper.toResponse(guardado);
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

        private void bloquearRolAdministrador()
        {
                rolRepository.findByNombreParaActualizar("ADMIN")
                        .orElseThrow(this::accesoAdministrativoDenegado);
        }

        private Usuario bloquearAdministradorActivo(
                String correoAdministrador
        )
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

        private AccessDeniedException accesoAdministrativoDenegado()
        {
                return new AccessDeniedException(
                        "El usuario no puede administrar usuarios"
                );
        }

        private RecursoNoEncontradoException usuarioNoEncontrado(Long id)
        {
                return new RecursoNoEncontradoException(
                        "No se encontró el usuario con ID " + id
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
                                "size debe estar entre 1 y "
                                        + TAMANO_MAXIMO_PAGINA
                        );
                }
        }

        private String validarRol(String rol)
        {
                if (rol == null)
                {
                        return null;
                }
                if (!ROLES_PERMITIDOS.contains(rol))
                {
                        throw new ParametroInvalidoException(
                                "rol debe ser USUARIO, REFUGIO o ADMIN"
                        );
                }
                return rol;
        }

        private Boolean validarActivo(String activo)
        {
                if (activo == null)
                {
                        return null;
                }
                if ("true".equals(activo))
                {
                        return true;
                }
                if ("false".equals(activo))
                {
                        return false;
                }
                throw new ParametroInvalidoException(
                        "activo debe ser true o false"
                );
        }

        private Sort crearOrden(String sort)
        {
                if (sort == null)
                {
                        return Sort.by(
                                Sort.Order.desc("fechaRegistro"),
                                Sort.Order.desc("id")
                        );
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

        private Specification<Usuario> crearFiltros(
                String texto,
                String rol,
                Boolean activo
        )
        {
                Specification<Usuario> filtros = (
                        root,
                        query,
                        builder
                ) -> builder.conjunction();

                if (texto != null && !texto.isBlank())
                {
                        String patron = "%" + escaparLike(
                                texto.trim().toLowerCase(Locale.ROOT)
                        ) + "%";
                        filtros = filtros.and((root, query, builder) ->
                                builder.or(
                                        builder.like(
                                                builder.lower(root.get("nombre")),
                                                patron,
                                                '\\'
                                        ),
                                        builder.like(
                                                builder.lower(root.get("apellidoPaterno")),
                                                patron,
                                                '\\'
                                        ),
                                        builder.like(
                                                builder.lower(root.get("apellidoMaterno")),
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
                if (rol != null)
                {
                        filtros = filtros.and((root, query, builder) ->
                                builder.equal(
                                        root.get("rol").get("nombre"),
                                        rol
                                ));
                }
                if (activo != null)
                {
                        filtros = filtros.and((root, query, builder) ->
                                builder.equal(root.get("activo"), activo));
                }

                return filtros;
        }

        private String escaparLike(String texto)
        {
                return texto
                        .replace("\\", "\\\\")
                        .replace("%", "\\%")
                        .replace("_", "\\_");
        }

        private String validarMotivo(String motivo)
        {
                if (motivo == null || motivo.isBlank())
                {
                        throw new ParametroInvalidoException(
                                "El motivo es obligatorio para cambiar el estado"
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

        private String normalizarCorreo(String correo)
        {
                if (correo == null)
                {
                        throw accesoAdministrativoDenegado();
                }
                return correo.trim().toLowerCase(Locale.ROOT);
        }

        private Usuario buscarActivoParaActualizar(
                String correoAutenticado
        )
        {
                return usuarioRepository
                        .findActivoPorCorreoParaActualizar(
                                correoAutenticado.trim().toLowerCase()
                        )
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "No se encontró el usuario autenticado"
                                )
                        );
        }

        private void registrarReemplazoTrasTransaccion(
                String fotoAnterior,
                String fotoNueva
        )
        {
                try
                {
                        verificarSincronizacionActiva();
                        TransactionSynchronizationManager
                                .registerSynchronization(
                                        new TransactionSynchronization()
                                        {
                                                @Override
                                                public void afterCompletion(
                                                        int status
                                                )
                                                {
                                                        if (status == STATUS_COMMITTED)
                                                        {
                                                                eliminarFotoSinPropagar(
                                                                        fotoAnterior
                                                                );
                                                        }
                                                        else
                                                        {
                                                                eliminarFotoSinPropagar(
                                                                        fotoNueva
                                                                );
                                                        }
                                                }
                                        }
                                );
                }
                catch (RuntimeException exception)
                {
                        eliminarFotoSinPropagar(fotoNueva);
                        throw exception;
                }
        }

        private void registrarEliminacionTrasCommit(
                String fotoAnterior
        )
        {
                verificarSincronizacionActiva();
                TransactionSynchronizationManager.registerSynchronization(
                        new TransactionSynchronization()
                        {
                                @Override
                                public void afterCompletion(int status)
                                {
                                        if (status == STATUS_COMMITTED)
                                        {
                                                eliminarFotoSinPropagar(
                                                        fotoAnterior
                                                );
                                        }
                                }
                        }
                );
        }

        private void verificarSincronizacionActiva()
        {
                if (!TransactionSynchronizationManager
                        .isSynchronizationActive())
                {
                        throw new IllegalStateException(
                                "No existe una transacción activa para actualizar la foto"
                        );
                }
        }

        private void eliminarFotoSinPropagar(String fotoPerfil)
        {
                if (fotoPerfil == null)
                {
                        return;
                }

                try
                {
                        perfilFotoStorageService.eliminarSiExiste(fotoPerfil);
                }
                catch (RuntimeException exception)
                {
                        log.error(
                                "No fue posible limpiar una foto de perfil después de completar la transacción",
                                exception
                        );
                }
        }

        private String limpiarTextoOpcional(String valor)
        {
                if (valor == null || valor.isBlank())
                {
                        return null;
                }

                return valor.trim();
        }
}
