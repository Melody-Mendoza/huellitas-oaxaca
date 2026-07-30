package com.huellitasoaxaca.backend.repository;

import com.huellitasoaxaca.backend.entity.Favorito;
import com.huellitasoaxaca.backend.entity.FavoritoId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoritoRepository
        extends JpaRepository<Favorito, FavoritoId> {

    @Query(
            value = """
                    SELECT favorito
                    FROM Favorito favorito
                    JOIN FETCH favorito.mascota mascota
                    LEFT JOIN FETCH mascota.refugio
                    WHERE favorito.usuario.id = :usuarioId
                    ORDER BY favorito.fechaAgregado DESC,
                             favorito.id.mascotaId DESC
                    """,
            countQuery = """
                    SELECT COUNT(favorito)
                    FROM Favorito favorito
                    WHERE favorito.usuario.id = :usuarioId
                    """
    )
    Page<Favorito> findPaginaPorUsuario(
            @Param("usuarioId") Long usuarioId,
            Pageable pageable
    );
}
