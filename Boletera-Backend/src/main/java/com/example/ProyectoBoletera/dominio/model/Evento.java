package com.example.ProyectoBoletera.dominio.model;

import com.example.ProyectoBoletera.dominio.enums.CategoriaEvento;
import com.example.ProyectoBoletera.dominio.enums.EstadoEvento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(columnDefinition = "DATETIME")
    @NotNull
    private LocalDateTime fecha;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime fechaFin;
    private String imagenUrl;

    @Positive
    private int capacidadTotal;

    @Enumerated(EnumType.STRING)
    private CategoriaEvento categoria;

    @Enumerated(EnumType.STRING)
    private EstadoEvento estado;

    @ManyToOne
    @JoinColumn(name = "administrador_id")
    @JsonIgnore
    private Administrador administrador;

    @ManyToOne
    @JoinColumn(name = "lugar_id")
    private Lugar lugar;

    @OneToMany(mappedBy = "evento")
    private List<Boleto> boletos;

    public Evento(String nombre, String descripcion, LocalDateTime fecha, LocalDateTime fechaFin,
                  String imagenUrl, int capacidadTotal, CategoriaEvento categoria,
                  EstadoEvento estado, Administrador administrador, Lugar lugar) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.fechaFin = fechaFin;
        this.imagenUrl = imagenUrl;
        this.capacidadTotal = capacidadTotal;
        this.categoria = categoria;
        this.estado = estado;
        this.administrador = administrador;
        this.lugar = lugar;
    }
}