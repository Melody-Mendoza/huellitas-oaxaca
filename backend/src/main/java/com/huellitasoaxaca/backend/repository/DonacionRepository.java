package com.huellitasoaxaca.backend.repository;

import com.huellitasoaxaca.backend.entity.Donacion;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DonacionRepository extends JpaRepository<Donacion, Long> 
{
    @EntityGraph(attributePaths = "refugio")
    Optional<Donacion> findByUsuarioIdAndClaveIdempotencia(
            Long usuarioId,
            String claveIdempotencia
    );

    @Query(
            value = """
                    SELECT donacion
                    FROM Donacion donacion
                    JOIN FETCH donacion.refugio
                    WHERE donacion.usuario.id = :usuarioId
                    ORDER BY donacion.fecha DESC, donacion.id DESC
                    """,
            countQuery = """
                    SELECT COUNT(donacion)
                    FROM Donacion donacion
                    WHERE donacion.usuario.id = :usuarioId
                    """
    )
    Page<Donacion> findPaginaPropia(
            @Param("usuarioId") Long usuarioId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "refugio")
    Optional<Donacion> findByIdAndUsuarioId(Long id, Long usuarioId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT donacion
            FROM Donacion donacion
            JOIN FETCH donacion.refugio
            WHERE donacion.id = :donacionId
              AND donacion.usuario.id = :usuarioId
            """)
    Optional<Donacion> findPropiaParaActualizar(
            @Param("donacionId") Long donacionId,
            @Param("usuarioId") Long usuarioId
    );
}
