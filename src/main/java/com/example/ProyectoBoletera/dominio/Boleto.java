package com.example.ProyectoBoletera.dominio;

import java.util.List;

import jakarta.persistence.*;


@Entity
public class Boleto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tipo;
    private Double precio;
    private int capacidadMax;
    private int disponibles;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento evento;

    @OneToMany(mappedBy = "boleto")
    private List<BoletoCliente> boletosCliente;

    public Boleto() {
    }

    public Boleto(String tipo, Double precio, int capacidadMax, int disponibles, Evento evento) {
        this.tipo = tipo;
        this.precio = precio;
        this.capacidadMax = capacidadMax;
        this.disponibles = disponibles;
        this.evento = evento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public int getCapacidadMax() {
        return capacidadMax;
    }

    public void setCapacidadMax(int capacidadMax) {
        this.capacidadMax = capacidadMax;
    }

    public int getDisponibles() {
        return disponibles;
    }

    public void setDisponibles(int disponibles) {
        this.disponibles = disponibles;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public List<BoletoCliente> getBoletosCliente() {
        return boletosCliente;
    }

    public void setBoletosCliente(List<BoletoCliente> boletosCliente) {
        this.boletosCliente = boletosCliente;
    }
}
