package com.huellitasoaxaca.backend.repository;

import com.huellitasoaxaca.backend.entity.Mascota;
import com.huellitasoaxaca.backend.entity.enums.Especie;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

@Repository
public interface MascotaRepository extends
        JpaRepository<Mascota, Long>,
        JpaSpecificationExecutor<Mascota>
{

    @Override
    @EntityGraph(attributePaths = "refugio")
    Page<Mascota> findAll(
            Specification<Mascota> specification,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "refugio")
    Optional<Mascota> findByIdAndEstadoAndRefugioActivoTrueAndRefugioAprobadoTrue(
            Long id,
            EstadoMascota estado
    );

    @EntityGraph(attributePaths = "refugio")
    Optional<Mascota> findByIdAndRefugioId(Long id, Long refugioId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "refugio")
    @Query("""
            SELECT mascota
            FROM Mascota mascota
            WHERE mascota.id = :mascotaId
              AND mascota.refugio.id = :refugioId
            """)
    Optional<Mascota> findPropiaParaActualizar(
            @Param("mascotaId") Long mascotaId,
            @Param("refugioId") Long refugioId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "refugio")
    @Query("SELECT mascota FROM Mascota mascota WHERE mascota.id = :id")
    Optional<Mascota> findByIdParaSolicitud(@Param("id") Long id);

    @EntityGraph(attributePaths = "refugio")
    @Query("SELECT m FROM Mascota m WHERE m.id = :id")
    Optional<Mascota> findAdminDetalleById(@Param("id") Long id);

    List<Mascota> findByEstado(EstadoMascota estado);

    List<Mascota> findByEspecie(Especie especie);

    List<Mascota> findByRefugioId(Long refugioId);

    long countByRefugioId(Long refugioId);

    long countByRefugioIdAndEstado(
            Long refugioId,
            EstadoMascota estado
    );

    List<Mascota> findByNombreContainingIgnoreCase(String nombre);

    List<Mascota> findByEspecieAndEstado(
        Especie especie,
        EstadoMascota estado
    );
}
