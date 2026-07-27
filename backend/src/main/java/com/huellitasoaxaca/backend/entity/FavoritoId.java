package com.huellitasoaxaca.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoritoId implements Serializable 
{

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "mascota_id")
    private Long mascotaId;

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }

        if (!(objeto instanceof FavoritoId favoritoId)) {
            return false;
        }

        return Objects.equals(usuarioId, favoritoId.usuarioId)
                && Objects.equals(mascotaId, favoritoId.mascotaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, mascotaId);
    }
}