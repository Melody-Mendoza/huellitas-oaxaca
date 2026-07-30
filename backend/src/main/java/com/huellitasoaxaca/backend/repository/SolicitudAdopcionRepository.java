package com.huellitasoaxaca.backend.repository;

import com.huellitasoaxaca.backend.entity.SolicitudAdopcion;
import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcion, Long> 
{

    List<SolicitudAdopcion> findByUsuarioId(Long usuarioId);

    @EntityGraph(attributePaths = {
            "mascota",
            "mascota.refugio"
    })
    Page<SolicitudAdopcion> findByUsuarioId(
            Long usuarioId,
            Pageable pageable
    );

    List<SolicitudAdopcion> findByMascotaId(Long mascotaId);

    List<SolicitudAdopcion> findByEstado(EstadoSolicitud estado);

    List<SolicitudAdopcion> findByUsuarioIdAndEstado(
            Long usuarioId,
            EstadoSolicitud estado
    );

    boolean existsByUsuarioIdAndMascotaId(
            Long usuarioId,
            Long mascotaId
    );

    boolean existsByUsuarioIdAndMascotaIdAndEstadoIn(
            Long usuarioId,
            Long mascotaId,
            List<EstadoSolicitud> estados
    );

    @EntityGraph(attributePaths = {
            "mascota",
            "mascota.refugio"
    })
    Optional<SolicitudAdopcion> findByIdAndUsuarioId(
            Long id,
            Long usuarioId
    );

    @Query("""
            SELECT COUNT(solicitud)
            FROM SolicitudAdopcion solicitud
            WHERE solicitud.mascota.refugio.id = :refugioId
            """)
    long countSolicitudesPorRefugio(
            @Param("refugioId") Long refugioId
    );

    @Query("""
            SELECT COUNT(solicitud)
            FROM SolicitudAdopcion solicitud
            WHERE solicitud.mascota.refugio.id = :refugioId
              AND solicitud.estado = :estado
            """)
    long countSolicitudesPorRefugioYEstado(
            @Param("refugioId") Long refugioId,
            @Param("estado") EstadoSolicitud estado
    );
}
