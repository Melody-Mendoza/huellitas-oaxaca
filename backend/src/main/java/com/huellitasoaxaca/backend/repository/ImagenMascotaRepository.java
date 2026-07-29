package com.huellitasoaxaca.backend.repository;

import com.huellitasoaxaca.backend.entity.ImagenMascota;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagenMascotaRepository extends JpaRepository<ImagenMascota, Long> 
{

    List<ImagenMascota> findByMascotaId(Long mascotaId);

    List<ImagenMascota> findByMascotaIdOrderByIdAsc(Long mascotaId);

    void deleteByMascotaId(Long mascotaId);
}
