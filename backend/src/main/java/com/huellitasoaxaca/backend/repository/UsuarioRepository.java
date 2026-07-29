package com.huellitasoaxaca.backend.repository;

import com.huellitasoaxaca.backend.entity.Usuario;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> 
{

    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByFirebaseUid(String firebaseUid);

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    boolean existsByCorreoAndActivoTrue(String correo);

    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByCorreoAndActivoTrue(String correo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "rol")
    @Query("""
            SELECT usuario
            FROM Usuario usuario
            WHERE usuario.correo = :correo
              AND usuario.activo = true
            """)
    Optional<Usuario> findActivoPorCorreoParaActualizar(
            @Param("correo") String correo
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "rol")
    @Query("""
            SELECT usuario
            FROM Usuario usuario
            WHERE usuario.correo = :correo
            """)
    Optional<Usuario> findPorCorreoParaActualizar(
            @Param("correo") String correo
    );

    List<Usuario> findByActivoTrue();
}
