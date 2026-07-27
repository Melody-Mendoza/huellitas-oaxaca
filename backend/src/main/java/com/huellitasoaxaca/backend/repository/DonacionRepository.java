package com.huellitasoaxaca.backend.repository;

import com.huellitasoaxaca.backend.entity.Donacion;
import com.huellitasoaxaca.backend.entity.enums.EstatusDonacion;
import com.huellitasoaxaca.backend.entity.enums.MetodoPago;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonacionRepository extends JpaRepository<Donacion, Long> 
{
        List<Donacion> findByUsuarioId(Long usuarioId);

        List<Donacion> findByRefugioId(Long refugioId);

        List<Donacion> findByEstatus(EstatusDonacion estatus);

        List<Donacion> findByMetodoPago(MetodoPago metodoPago);

        List<Donacion> findByRefugioIdAndEstatus(
                Long refugioId,
                EstatusDonacion estatus
        );

        List<Donacion> findByFechaBetween(
                LocalDateTime fechaInicio,
                LocalDateTime fechaFin
        );
}