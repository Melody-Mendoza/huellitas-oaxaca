package com.huellitasoaxaca.backend.services.impl;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.dto.request.LoginRequest;
import com.huellitasoaxaca.backend.dto.request.UsuarioRegistroRequest;
import com.huellitasoaxaca.backend.dto.response.AuthResponse;
import com.huellitasoaxaca.backend.dto.response.UsuarioResponse;
import com.huellitasoaxaca.backend.entity.Rol;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.exception.RecursoDuplicadoException;
import com.huellitasoaxaca.backend.exception.RecursoNoEncontradoException;
import com.huellitasoaxaca.backend.mapper.UsuarioMapper;
import com.huellitasoaxaca.backend.repository.RolRepository;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
import com.huellitasoaxaca.backend.security.JwtService;
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

                Authentication authentication =
                        authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                        correoNormalizado,
                                        request.password()
                                )
                        );

                UserDetails userDetails =
                        (UserDetails) authentication.getPrincipal();

                Usuario usuario = usuarioRepository
                        .findByCorreoAndActivoTrue(correoNormalizado)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "No se encontró el usuario autenticado"
                                )
                        );

                String token = jwtService.generarToken(userDetails);

                return new AuthResponse(
                        token,
                        "Bearer",
                        jwtService.obtenerExpiracionEnSegundos(),
                        usuarioMapper.toResponse(usuario)
                );
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
