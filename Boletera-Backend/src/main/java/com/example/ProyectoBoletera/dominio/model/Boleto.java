package com.example.ProyectoBoletera.dominio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String tipo;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    @PositiveOrZero
    private int capacidadMax;

    @PositiveOrZero
    private int disponibles;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    @JsonIgnore
    private Evento evento;

    @OneToMany(mappedBy = "boleto")
    private List<BoletoCliente> boletosCliente;

}
