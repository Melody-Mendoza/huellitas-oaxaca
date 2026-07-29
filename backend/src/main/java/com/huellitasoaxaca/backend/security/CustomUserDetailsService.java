package com.huellitasoaxaca.backend.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService
{
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository
                .findByCorreoAndActivoTrue(correo)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Correo o contraseña incorrectos"
                        )
                );

        String autoridad = "ROLE_" + usuario.getRol().getNombre();

        return User.builder()
                .username(usuario.getCorreo())
                .password(
                        usuario.getPassword() == null
                                ? ""
                                : usuario.getPassword()
                )
                .authorities(autoridad)
                .disabled(!usuario.getActivo())
                .build();
    }
}
