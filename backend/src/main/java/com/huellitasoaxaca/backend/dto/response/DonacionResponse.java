package com.huellitasoaxaca.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.huellitasoaxaca.backend.entity.enums.EstatusDonacion;
import com.huellitasoaxaca.backend.entity.enums.MetodoPago;

public record DonacionResponse(
    Long id,
    BigDecimal monto,
    MetodoPago metodoPago,
    LocalDateTime fecha,
    EstatusDonacion estatus,
    String mensaje,
    Long usuarioId,
    String nombreUsuario,
    Long refugioId,
    String nombreRefugio
) 
{}
