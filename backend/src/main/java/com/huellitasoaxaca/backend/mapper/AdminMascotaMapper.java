package com.huellitasoaxaca.backend.mapper;

import org.springframework.stereotype.Component;

import com.huellitasoaxaca.backend.dto.response.MascotaAdminDetalleResponse;
import com.huellitasoaxaca.backend.dto.response.MascotaAdminResumenResponse;
import com.huellitasoaxaca.backend.entity.Mascota;
import com.huellitasoaxaca.backend.entity.Refugio;

@Component
public class AdminMascotaMapper
{
    public MascotaAdminResumenResponse toAdminResumen(Mascota mascota)
    {
        Refugio refugio = mascota.getRefugio();
        return new MascotaAdminResumenResponse(
                mascota.getId(),
                mascota.getNombre(),
                mascota.getEspecie(),
                mascota.getRaza(),
                mascota.getSexo(),
                mascota.getEdad(),
                mascota.getTamano(),
                mascota.getEstado(),
                mascota.getFechaIngreso(),
                mascota.getImagen(),
                refugio != null ? refugio.getId() : null,
                refugio != null ? refugio.getNombre() : null
        );
    }

    public MascotaAdminDetalleResponse toAdminDetalle(Mascota mascota)
    {
        Refugio refugio = mascota.getRefugio();
        return new MascotaAdminDetalleResponse(
                mascota.getId(),
                mascota.getNombre(),
                mascota.getEspecie(),
                mascota.getRaza(),
                mascota.getSexo(),
                mascota.getEdad(),
                mascota.getPeso(),
                mascota.getTamano(),
                mascota.getDescripcion(),
                mascota.getEstado(),
                mascota.getFechaIngreso(),
                mascota.getImagen(),
                refugio != null ? refugio.getId() : null,
                refugio != null ? refugio.getNombre() : null,
                refugio != null ? refugio.getCorreo() : null,
                refugio != null ? refugio.getTelefono() : null
        );
    }
}
