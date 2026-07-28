package com.huellitasoaxaca.backend.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.huellitasoaxaca.backend.entity.TokenRecuperacionPassword;

@Repository
public interface TokenRecuperacionPasswordRepository extends JpaRepository<TokenRecuperacionPassword, Long>
{
    Optional<TokenRecuperacionPassword> findByToken(String token);

    Optional<TokenRecuperacionPassword>
            findByTokenAndUtilizadoFalse(String token);

    long deleteByFechaExpiracionBefore(LocalDateTime fecha);

    void deleteByUsuarioIdAndUtilizadoFalse(Long usuarioId);
}
