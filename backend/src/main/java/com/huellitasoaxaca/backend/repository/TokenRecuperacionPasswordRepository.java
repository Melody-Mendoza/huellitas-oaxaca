package com.huellitasoaxaca.backend.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.huellitasoaxaca.backend.entity.TokenRecuperacionPassword;

import jakarta.persistence.LockModeType;

@Repository
public interface TokenRecuperacionPasswordRepository extends JpaRepository<TokenRecuperacionPassword, Long>
{
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TokenRecuperacionPassword>
            findByTokenHashAndUtilizadoFalse(String tokenHash);

    long deleteByFechaExpiracionBefore(LocalDateTime fecha);

    void deleteByUsuarioIdAndUtilizadoFalse(Long usuarioId);
}
