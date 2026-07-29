package com.huellitasoaxaca.backend.repository;

import com.huellitasoaxaca.backend.entity.SolicitudAdopcion;
import com.huellitasoaxaca.backend.entity.enums.EstadoSolicitud;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcion, Long> 
{

    List<SolicitudAdopcion> findByUsuarioId(Long usuarioId);

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

    Optional<SolicitudAdopcion> findByIdAndUsuarioId(
            Long id,
            Long usuarioId
    );
}
