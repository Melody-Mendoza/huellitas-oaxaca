package com.huellitasoaxaca.backend.repository;

import com.huellitasoaxaca.backend.entity.Rol;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT rol FROM Rol rol WHERE rol.nombre = :nombre")
    Optional<Rol> findByNombreParaActualizar(@Param("nombre") String nombre);

    boolean existsByNombre(String nombre);
}
