package com.example.ProyectoBoletera.dominio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Zona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @Positive
    private int filas;

    @Positive
    private int asientosPorFila;

    @ManyToOne
    @JoinColumn(name = "lugar_id")
    @JsonIgnore
    private Lugar lugar;

    public Zona(String nombre, int filas, int asientosPorFila, Lugar lugar) {
        this.nombre = nombre;
        this.filas = filas;
        this.asientosPorFila = asientosPorFila;
        this.lugar = lugar;
    }

    public int getCapacidad() {
        return filas * asientosPorFila;
    }
}
