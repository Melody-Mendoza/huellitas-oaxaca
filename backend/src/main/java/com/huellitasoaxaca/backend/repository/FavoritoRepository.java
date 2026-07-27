package com.huellitasoaxaca.backend.repository;

import com.huellitasoaxaca.backend.entity.Favorito;
import com.huellitasoaxaca.backend.entity.FavoritoId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoritoRepository
        extends JpaRepository<Favorito, FavoritoId> {

    List<Favorito> findByUsuarioId(Long usuarioId);

    List<Favorito> findByMascotaId(Long mascotaId);

    boolean existsByUsuarioIdAndMascotaId(
            Long usuarioId,
            Long mascotaId
    );

    void deleteByUsuarioIdAndMascotaId(
            Long usuarioId,
            Long mascotaId
    );
}
