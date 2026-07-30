package com.huellitasoaxaca.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.huellitasoaxaca.backend.entity.Refugio;

import jakarta.persistence.LockModeType;


@Repository
public interface RefugioRepository extends
        JpaRepository<Refugio, Long>,
        JpaSpecificationExecutor<Refugio>
{

    @Override
    @EntityGraph(attributePaths = {
            "usuario",
            "usuario.rol",
            "aprobadoPor"
    })
    Page<Refugio> findAll(
            Specification<Refugio> specification,
            Pageable pageable
    );

    Optional<Refugio> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<Refugio> findByActivoTrue();

    List<Refugio> findByActivoTrueAndAprobadoTrueOrderByNombreAscIdAsc();

    List<Refugio> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndActivoTrue(Long usuarioId);

    List<Refugio> findByUsuarioIdAndActivoTrueOrderByNombreAscIdAsc(
            Long usuarioId
    );

    Optional<Refugio> findByIdAndUsuarioId(
            Long id,
            Long usuarioId
    );

    @EntityGraph(attributePaths = {
            "usuario",
            "usuario.rol",
            "aprobadoPor"
    })
    @Query("SELECT refugio FROM Refugio refugio WHERE refugio.id = :id")
    Optional<Refugio> findDetalleAdministrativoById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {
            "usuario",
            "usuario.rol",
            "aprobadoPor"
    })
    @Query("SELECT refugio FROM Refugio refugio WHERE refugio.id = :id")
    Optional<Refugio> findByIdParaActualizar(@Param("id") Long id);
}
