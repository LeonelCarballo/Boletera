package com.example.ProyectoBoletera.dominio.model;

import com.example.ProyectoBoletera.dominio.enums.RolUsuario;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Cliente extends Usuario {

    @OneToMany(mappedBy = "cliente")
    private List<Compra> compras;

    public Cliente(String nombre, String email, String contrasenia, String telefono) {
        super(nombre, email, contrasenia, telefono, RolUsuario.CLIENTE);
    }
}