package com.example.ProyectoBoletera.dominio.model;

import com.example.ProyectoBoletera.dominio.enums.RolUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 250)
    private String nombre;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String contrasenia;

    @Size(min = 10, max = 10)
    private String telefono;

    @Enumerated(EnumType.STRING)
    private RolUsuario rol;

    public Usuario(String nombre, String email, String contrasenia, String telefono, RolUsuario rol) {
        this.nombre = nombre;
        this.email = email;
        this.contrasenia = contrasenia;
        this.telefono = telefono;
        this.rol = rol;
    }
}