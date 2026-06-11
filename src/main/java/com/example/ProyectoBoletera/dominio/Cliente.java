package com.example.ProyectoBoletera.dominio;

import java.util.List;

@Entity
public class Cliente extends Usuario {
    @OneToMany(mappedBy = "cliente")
    private List<Compra> compras;

    public Cliente() {
    }

    public Cliente(String nombre, String email, String contrasena, String telefono) {
        super(nombre, email, contrasena, telefono);
    }

    public List<Compra> getCompras() {
        return compras;
    }

    public void setCompras(List<Compra> compras) {
        this.compras = compras;
    }
}
