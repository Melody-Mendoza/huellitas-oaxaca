package com.huellitasoaxaca.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.huellitasoaxaca.backend.entity.AuditoriaAdministrativa;

public interface AuditoriaAdministrativaRepository
        extends JpaRepository<AuditoriaAdministrativa, Long>,
        JpaSpecificationExecutor<AuditoriaAdministrativa>
{
    @Override
    @EntityGraph(attributePaths = "administrador")
    Page<AuditoriaAdministrativa> findAll(
            Specification<AuditoriaAdministrativa> spec,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "administrador")
    @Query("SELECT a FROM AuditoriaAdministrativa a WHERE a.id = :id")
    Optional<AuditoriaAdministrativa> findAdminDetalleById(@Param("id") Long id);
}
