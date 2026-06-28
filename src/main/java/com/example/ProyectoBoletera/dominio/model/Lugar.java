package com.example.ProyectoBoletera.dominio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Lugar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String direccion;

    @NotBlank
    private String ciudad;

    @NotBlank
    private String estado;

    @Positive
    private Integer capacidad;

    @OneToMany(mappedBy = "lugar")
    @JsonIgnore
    private List<Evento> eventos;

    public Lugar(String nombre, String direccion, String ciudad, String estado, Integer capacidad) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.estado = estado;
        this.capacidad = capacidad;
    }
}