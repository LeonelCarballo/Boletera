package com.example.ProyectoBoletera.dominio.model;

import com.example.ProyectoBoletera.dominio.enums.EstadoBoleto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor

@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"boleto_id", "asiento_id"}))
public class BoletoCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String codigoQr;

    @Enumerated(EnumType.STRING)
    private EstadoBoleto estado;

    @ManyToOne
    @JoinColumn(name = "compra_id")
    @JsonIgnore
    private Compra compra;

    @ManyToOne
    @JoinColumn(name = "boleto_id")
    @JsonIgnore
    private Boleto boleto;

    @ManyToOne
    @JoinColumn(name = "asiento_id")
    private Asiento asiento;

    public BoletoCliente(String codigoQr, EstadoBoleto estado, Compra compra, Boleto boleto) {
        this.codigoQr = codigoQr;
        this.estado = estado;
        this.compra = compra;
        this.boleto = boleto;
    }
}