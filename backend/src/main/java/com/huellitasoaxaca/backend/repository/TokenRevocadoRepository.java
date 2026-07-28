package com.huellitasoaxaca.backend.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.huellitasoaxaca.backend.entity.TokenRevocado;

@Repository
public interface TokenRevocadoRepository extends JpaRepository<TokenRevocado, Long>
{
    boolean existsByJti(String jti);

    long deleteByFechaExpiracionBefore(LocalDateTime fecha);
}
