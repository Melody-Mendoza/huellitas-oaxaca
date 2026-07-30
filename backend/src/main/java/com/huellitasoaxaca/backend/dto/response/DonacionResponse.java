package com.huellitasoaxaca.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.huellitasoaxaca.backend.entity.enums.EstatusDonacion;
import com.huellitasoaxaca.backend.entity.enums.MetodoPago;

public record DonacionResponse(
    Long id,
    BigDecimal monto,
    String moneda,
    MetodoPago metodoPago,
    LocalDateTime fecha,
    LocalDateTime fechaActualizacion,
    EstatusDonacion estatus,
    String mensaje,
    Long refugioId,
    String nombreRefugio
) 
{}
