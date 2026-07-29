package com.huellitasoaxaca.backend.services.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.huellitasoaxaca.backend.dto.request.GoogleLoginRequest;
import com.huellitasoaxaca.backend.dto.request.LoginRequest;
import com.huellitasoaxaca.backend.dto.request.UsuarioRegistroRequest;
import com.huellitasoaxaca.backend.dto.response.AuthResponse;
import com.huellitasoaxaca.backend.dto.response.UsuarioResponse;
import com.huellitasoaxaca.backend.entity.Rol;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.exception.RecursoDuplicadoException;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.exception.GoogleAuthenticationException;
import com.huellitasoaxaca.backend.mapper.UsuarioMapper;
import com.huellitasoaxaca.backend.repository.RolRepository;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
import com.huellitasoaxaca.backend.security.JwtService;
import com.huellitasoaxaca.backend.security.FirebaseTokenVerifier;
import com.huellitasoaxaca.backend.security.FirebaseTokenVerifier.FirebaseIdentity;
import com.huellitasoaxaca.backend.services.AuthService;
import com.huellitasoaxaca.backend.services.RecuperacionPasswordService;
import com.huellitasoaxaca.backend.services.TokenRevocadoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService
{
        private final UsuarioRepository usuarioRepository;
        private final RolRepository rolRepository;
        private final UsuarioMapper usuarioMapper;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;
        private final TokenRevocadoService tokenRevocadoService;
        private final RecuperacionPasswordService recuperacionPasswordService;
        private final FirebaseTokenVerifier firebaseTokenVerifier;
        private final TransactionTemplate transactionTemplate;

        @Override
        @Transactional
        public UsuarioResponse registrar(UsuarioRegistroRequest request) 
        {
                String correoNormalizado = request.correo()
                        .trim()
                        .toLowerCase();

                if (usuarioRepository.existsByCorreo(correoNormalizado)) 
                {
                throw new RecursoDuplicadoException(
                        "Ya existe un usuario registrado con ese correo"
                );
                }

                Rol rolUsuario = rolRepository.findByNombre("USUARIO")
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "No se encontró el rol USUARIO"
                                )
                        );

                Usuario usuario = new Usuario();

                usuario.setNombre(request.nombre().trim());
                usuario.setApellidoPaterno(
                        request.apellidoPaterno().trim()
                );
                usuario.setApellidoMaterno(
                        limpiarTextoOpcional(request.apellidoMaterno())
                );
                usuario.setCorreo(correoNormalizado);
                usuario.setPassword(
                        passwordEncoder.encode(request.password())
                );
                usuario.setTelefono(
                        limpiarTextoOpcional(request.telefono())
                );
                usuario.setActivo(true);
                usuario.setFechaRegistro(LocalDateTime.now());
                usuario.setRol(rolUsuario);

                Usuario usuarioGuardado =
                        usuarioRepository.save(usuario);

                return usuarioMapper.toResponse(usuarioGuardado);
        }

        @Override
        @Transactional(readOnly = true)
        public AuthResponse login(LoginRequest request) 
        {
                String correoNormalizado = request.correo()
                        .trim()
                        .toLowerCase();

                Usuario usuario = usuarioRepository
                        .findByCorreoAndActivoTrue(correoNormalizado)
                        .orElseThrow(() ->
                                new BadCredentialsException(
                                        "Correo o contraseña incorrectos"
                                )
                        );

                if (usuario.getPassword() == null)
                {
                        throw new BadCredentialsException(
                                "Correo o contraseña incorrectos"
                        );
                }

                Authentication authentication =
                        authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                        correoNormalizado,
                                        request.password()
                                )
                        );

                UserDetails userDetails =
                        (UserDetails) authentication.getPrincipal();

                String token = jwtService.generarToken(userDetails);

                return new AuthResponse(
                        token,
                        "Bearer",
                        jwtService.obtenerExpiracionEnSegundos(),
                        usuarioMapper.toResponse(usuario)
                );
        }

        @Override
        public AuthResponse loginGoogle(GoogleLoginRequest request)
        {
                FirebaseIdentity identity =
                        firebaseTokenVerifier.verify(request.idToken());

                Usuario usuario;

                try
                {
                        usuario = transactionTemplate.execute(
                                status -> resolverUsuarioGoogle(identity)
                        );
                }
                catch (DataIntegrityViolationException exception)
                {
                        usuario = transactionTemplate.execute(
                                status -> reconciliarConflictoGoogle(identity)
                        );
                }

                if (usuario == null)
                {
                        throw new IllegalStateException(
                                "No fue posible resolver el usuario Google"
                        );
                }

                String token = jwtService.generarToken(usuario);

                return new AuthResponse(
                        token,
                        "Bearer",
                        jwtService.obtenerExpiracionEnSegundos(),
                        usuarioMapper.toResponse(usuario)
                );
        }

        private Usuario resolverUsuarioGoogle(FirebaseIdentity identity)
        {
                Optional<Usuario> porUid = usuarioRepository
                        .findByFirebaseUid(identity.uid());

                if (porUid.isPresent())
                {
                        return validarUsuarioPorUid(
                                porUid.get(),
                                identity
                        );
                }

                Optional<Usuario> porCorreo = usuarioRepository
                        .findPorCorreoParaActualizar(identity.correo());

                if (porCorreo.isPresent())
                {
                        Usuario usuario = porCorreo.get();

                        validarActivo(usuario);

                        String rol = usuario.getRol().getNombre();

                        if ("ADMIN".equals(rol) || "REFUGIO".equals(rol))
                        {
                                throw conflict(
                                        "La cuenta requiere vinculación manual"
                                );
                        }

                        if (!"USUARIO".equals(rol))
                        {
                                throw conflict(
                                        "El rol no permite vinculación automática"
                                );
                        }

                        if (usuario.getFirebaseUid() != null)
                        {
                                if (usuario.getFirebaseUid().equals(identity.uid()))
                                {
                                        return validarUsuarioPorUid(
                                                usuario,
                                                identity
                                        );
                                }

                                throw conflict(
                                        "El correo ya está vinculado con otra cuenta Google"
                                );
                        }

                        usuario.setFirebaseUid(identity.uid());

                        return usuarioRepository.saveAndFlush(usuario);
                }

                Rol rolUsuario = rolRepository.findByNombre("USUARIO")
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "No se encontró el rol USUARIO"
                                )
                        );

                Usuario usuario = new Usuario();

                usuario.setNombre(nombreGoogleSeguro(identity.nombre()));
                usuario.setApellidoPaterno("Google");
                usuario.setApellidoMaterno(null);
                usuario.setCorreo(identity.correo());
                usuario.setFirebaseUid(identity.uid());
                usuario.setPassword(null);
                usuario.setTelefono(null);
                usuario.setFotoPerfil(null);
                usuario.setActivo(true);
                usuario.setFechaRegistro(LocalDateTime.now());
                usuario.setRol(rolUsuario);

                return usuarioRepository.saveAndFlush(usuario);
        }

        private Usuario validarUsuarioPorUid(
                Usuario usuario,
                FirebaseIdentity identity
        )
        {
                if (!usuario.getCorreo().equals(identity.correo()))
                {
                        throw conflict(
                                "El UID de Google no coincide con el correo registrado"
                        );
                }

                validarActivo(usuario);

                return usuario;
        }

        private void validarActivo(Usuario usuario)
        {
                if (!Boolean.TRUE.equals(usuario.getActivo()))
                {
                        throw new GoogleAuthenticationException(
                                HttpStatus.FORBIDDEN,
                                "La cuenta de usuario está inactiva"
                        );
                }
        }

        private Usuario reconciliarConflictoGoogle(
                FirebaseIdentity identity
        )
        {
                return usuarioRepository
                        .findByFirebaseUid(identity.uid())
                        .map(usuario -> validarUsuarioPorUid(
                                usuario,
                                identity
                        ))
                        .orElseThrow(() -> conflict(
                                "La cuenta se modificó durante la vinculación"
                        ));
        }

        private GoogleAuthenticationException conflict(String message)
        {
                return new GoogleAuthenticationException(
                        HttpStatus.CONFLICT,
                        message
                );
        }

        private String nombreGoogleSeguro(String nombre)
        {
                String value = nombre == null ? "" : nombre.trim();

                if (value.isBlank())
                {
                        return "Usuario";
                }

                return value.length() <= 100
                        ? value
                        : value.substring(0, 100);
        }

        private String limpiarTextoOpcional(String valor) 
        {
                if (valor == null || valor.isBlank()) 
                {
                return null;
                }

                return valor.trim();
        }

        @Override
        @Transactional(readOnly = true)
        public UsuarioResponse obtenerUsuarioAutenticado(String correo) 
        {
        Usuario usuario = usuarioRepository
                .findByCorreoAndActivoTrue(
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
        @Transactional
        public void logout(
                String jti,
                String correo,
                Instant fechaExpiracion
        ) 
        {
                tokenRevocadoService.revocar(
                        jti,
                        correo,
                        fechaExpiracion
                );
        }

        @Override
        public void solicitarRecuperacionPassword(String correo) 
        {
                recuperacionPasswordService.solicitarRecuperacion(correo);
        }

        @Override
        public void restablecerPassword(
                String token,
                String nuevaPassword,
                String confirmarPassword
        ) 
        {
                recuperacionPasswordService.restablecerPassword(
                        token,
                        nuevaPassword,
                        confirmarPassword
                );
        }
}
