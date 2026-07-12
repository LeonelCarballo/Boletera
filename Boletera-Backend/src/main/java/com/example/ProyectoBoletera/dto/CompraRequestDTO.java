package com.example.ProyectoBoletera.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CompraRequestDTO {
    private List<ItemCompraDTO> boletos;
}
