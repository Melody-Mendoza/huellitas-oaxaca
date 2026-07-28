package com.huellitasoaxaca.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tokens_revocados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRevocado 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "jti",
            nullable = false,
            unique = true,
            length = 100
    )
    private String jti;

    @Column(
            name = "correo_usuario",
            nullable = false,
            length = 150
    )
    private String correoUsuario;

    @Column(
            name = "fecha_revocacion",
            nullable = false
    )
    private LocalDateTime fechaRevocacion;

    @Column(
            name = "fecha_expiracion",
            nullable = false
    )
    private LocalDateTime fechaExpiracion;
}
