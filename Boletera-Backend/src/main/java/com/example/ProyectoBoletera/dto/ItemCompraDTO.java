package com.example.ProyectoBoletera.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCompraDTO {
    private Long boletoId;
    private int cantidad;

    private List<Long> asientos;
}
