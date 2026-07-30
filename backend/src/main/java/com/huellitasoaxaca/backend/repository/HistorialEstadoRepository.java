package com.huellitasoaxaca.backend.repository;

import com.huellitasoaxaca.backend.entity.HistorialEstado;
import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> 
{

    List<HistorialEstado> findBySolicitudIdOrderByFechaAsc(
            Long solicitudId
    );

    List<HistorialEstado> findByEstado(
            EstadoSolicitud estado
    );

    @Query("""
            SELECT historial
            FROM HistorialEstado historial
            JOIN historial.solicitud solicitud
            WHERE solicitud.id = :solicitudId
              AND solicitud.usuario.id = :usuarioId
            ORDER BY historial.fecha ASC, historial.id ASC
            """)
    List<HistorialEstado> findHistorialPropioOrdenado(
            @Param("solicitudId") Long solicitudId,
            @Param("usuarioId") Long usuarioId
    );
}
