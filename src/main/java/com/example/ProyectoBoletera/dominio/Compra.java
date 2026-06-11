package com.example.ProyectoBoletera.dominio;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate fechaCompra;
    private Double total;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "compra")
    private List<BoletoCliente> boletosCliente;

    public Compra() {
    }

    public Compra(LocalDate fechaCompra, Double total, Cliente cliente) {
        this.fechaCompra = fechaCompra;
        this.total = total;
        this.cliente = cliente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<BoletoCliente> getBoletosCliente() {
        return boletosCliente;
    }

    public void setBoletosCliente(List<BoletoCliente> boletosCliente) {
        this.boletosCliente = boletosCliente;
    }
}
