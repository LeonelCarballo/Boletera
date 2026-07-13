package com.example.ProyectoBoletera.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private Long boletoClienteId;
    private String codigoQr;
    private String estado;

    private String tipoBoleto;
    private BigDecimal precio;

    private Long eventoId;
    private String eventoNombre;
    private LocalDateTime eventoFecha;

    private String lugarNombre;
    private String lugarCiudad;

    private LocalDateTime fechaCompra;

    private String zonaNombre;
    private String asiento;
}
