package com.example.ProyectoBoletera.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponseDTO {
    private Long compraId;
    private BigDecimal total;
    private LocalDateTime fechaCompra;
    private List<TicketDTO> tickets;
}
