package com.example.ProyectoBoletera.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Organizador extends Usuario {
    private String empresa;

    @OneToMany(mappedBy = "organizador")
    private List<Evento> eventos;

    public Organizador() {
    }

    public Organizador(String nombre, String email, String contrasenia, String telefono, String empresa) {
        super(nombre, email, contrasenia, telefono);
        this.empresa = empresa;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
    }
}
