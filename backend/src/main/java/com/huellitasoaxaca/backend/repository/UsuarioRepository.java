package com.huellitasoaxaca.backend.repository;

import com.huellitasoaxaca.backend.entity.Usuario;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> 
{

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    boolean existsByCorreoAndActivoTrue(String correo);

    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByCorreoAndActivoTrue(String correo);

    List<Usuario> findByActivoTrue();
}
