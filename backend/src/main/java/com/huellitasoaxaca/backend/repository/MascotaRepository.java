package com.huellitasoaxaca.backend.repository;

import com.huellitasoaxaca.backend.entity.Mascota;
import com.huellitasoaxaca.backend.entity.enums.Especie;
import com.huellitasoaxaca.backend.entity.enums.EstadoMascota;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    List<Mascota> findByEstado(EstadoMascota estado);

    List<Mascota> findByEspecie(Especie especie);

    List<Mascota> findByRefugioId(Long refugioId);

    List<Mascota> findByNombreContainingIgnoreCase(String nombre);

    List<Mascota> findByEspecieAndEstado(
        Especie especie,
        EstadoMascota estado
    );
}
