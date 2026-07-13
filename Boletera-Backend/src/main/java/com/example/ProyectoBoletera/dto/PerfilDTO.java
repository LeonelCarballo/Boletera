package com.example.ProyectoBoletera.dto;

public class PerfilDTO {
    private String nombre;
    private String email;
    private String telefono;
    private String rol;
    private boolean activo;

    public PerfilDTO(String nombre, String email, String telefono, String rol) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.rol = rol;
    }

    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getRol() { return rol; }

    public boolean isActivo() {
        return activo;
    }
}
