package com.example.ProyectoBoletera.dominio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"zona_id", "fila", "numero"}))
public class Asiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fila;

    private int numero;

    @ManyToOne
    @JoinColumn(name = "zona_id")
    @JsonIgnore
    private Zona zona;

    public Asiento(String fila, int numero, Zona zona) {
        this.fila = fila;
        this.numero = numero;
        this.zona = zona;
    }

    public String getEtiqueta() {
        return fila + numero;
    }
}
