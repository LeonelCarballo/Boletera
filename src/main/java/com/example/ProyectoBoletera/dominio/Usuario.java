package com.example.ProyectoBoletera.dominio;

import jakarta.persistence.*;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String email;
    private String contrasenia;
    private String telefono;

    public Usuario() {

    }

    public Usuario(Long id, String nombre, String email, String contrasenia, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contrasenia = contrasenia;
        this.telefono = telefono;
    }

    public Usuario(String nombre, String email, String contrasenia, String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.contrasenia = contrasenia;
        this.telefono = telefono;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasenia;
    }

    public void setContrasena(String contrasena) {
        this.contrasenia = contrasena;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
