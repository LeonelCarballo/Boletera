package com.example.ProyectoBoletera.dominio;

import com.example.ProyectoBoletera.dominio.enums.EstadoEvento;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fecha;
    private String imagen;

    @Enumerated(EnumType.STRING)
    private EstadoEvento estado;

    @ManyToOne
    @JoinColumn(name = "organizador_id")
    private Organizador organizador;

    @ManyToOne
    @JoinColumn(name = "lugar_id")
    private Lugar lugar;

    @OneToMany(mappedBy = "evento")
    private List<Boleto> boletos;

    public Evento() {
    }

    public Evento(String nombre, String descripcion, LocalDateTime fecha, String imagen, EstadoEvento estado, Organizador organizador, Lugar lugar) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.imagen = imagen;
        this.estado = estado;
        this.organizador = organizador;
        this.lugar = lugar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public EstadoEvento getEstado() {
        return estado;
    }

    public void setEstado(EstadoEvento estado) {
        this.estado = estado;
    }

    public Organizador getOrganizador() {
        return organizador;
    }

    public void setOrganizador(Organizador organizador) {
        this.organizador = organizador;
    }

    public Lugar getLugar() {
        return lugar;
    }

    public void setLugar(Lugar lugar) {
        this.lugar = lugar;
    }

    public List<Boleto> getBoletos() {
        return boletos;
    }

    public void setBoletos(List<Boleto> boletos) {
        this.boletos = boletos;
    }
}
