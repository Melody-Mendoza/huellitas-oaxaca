package com.huellitasoaxaca.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.huellitasoaxaca.backend.entity.Refugio;


@Repository
public interface RefugioRepository extends JpaRepository<Refugio, Long> 
{

    Optional<Refugio> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<Refugio> findByActivoTrue();

    List<Refugio> findByUsuarioId(Long usuarioId);
}
