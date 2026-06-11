package com.example.ProyectoBoletera.dominio;

import jakarta.persistence.*;


@Entity
public class Administrador extends Usuario {

    public Administrador() {
    }

    public Administrador(String nombre, String email, String contrasena, String telefono) {
        super(nombre, email, contrasena, telefono);
    }
}
