package com.example.ProyectoBoletera.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZonaAsientosDTO {
    private Long boletoId;
    private String tipo;
    private BigDecimal precio;
    private Long zonaId;
    private String zonaNombre;
    private int filas;
    private int asientosPorFila;
    private List<AsientoDTO> asientos;
}
