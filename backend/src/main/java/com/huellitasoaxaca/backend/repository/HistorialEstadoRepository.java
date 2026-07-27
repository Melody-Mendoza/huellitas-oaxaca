package com.huellitasoaxaca.backend.repository;

import com.huellitasoaxaca.backend.entity.HistorialEstado;
import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
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
}