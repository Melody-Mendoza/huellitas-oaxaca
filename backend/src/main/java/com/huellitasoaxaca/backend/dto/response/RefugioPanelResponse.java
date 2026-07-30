package com.huellitasoaxaca.backend.dto.response;

public record RefugioPanelResponse(
        long totalMascotas,
        long mascotasDisponibles,
        long mascotasEnProceso,
        long mascotasAdoptadas,
        long totalSolicitudes,
        long solicitudesPendientes,
        long solicitudesAprobadas,
        long solicitudesRechazadas
) {}
