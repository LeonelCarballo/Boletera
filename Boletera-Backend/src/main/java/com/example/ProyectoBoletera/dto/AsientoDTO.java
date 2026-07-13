package com.example.ProyectoBoletera.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsientoDTO {
    private Long id;
    private String fila;
    private int numero;
    private boolean ocupado;
}
