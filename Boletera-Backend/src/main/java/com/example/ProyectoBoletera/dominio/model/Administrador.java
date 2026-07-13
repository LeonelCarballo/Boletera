package com.example.ProyectoBoletera.dominio.model;

import com.example.ProyectoBoletera.dominio.enums.RolUsuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Administrador extends Usuario {

    @OneToMany(mappedBy = "administrador")
    @JsonIgnore
    private List<Evento> eventos;

    public Administrador(String nombre, String email, String contrasenia, String telefono) {
        super(nombre, email, contrasenia, telefono, RolUsuario.ADMIN);
    }
}