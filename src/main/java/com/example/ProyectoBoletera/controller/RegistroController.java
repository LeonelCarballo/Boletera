package com.example.ProyectoBoletera.controller;

import com.example.ProyectoBoletera.dominio.model.Administrador;
import com.example.ProyectoBoletera.dominio.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistroController {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/crear-cuenta")
    public String crearCuenta(@RequestParam String nombre,
                              @RequestParam String correo,
                              @RequestParam String contrasenia,
                              @RequestParam String confirmar_contrasenia,
                              @RequestParam String telefono) {

        if (!contrasenia.equals(confirmar_contrasenia)) {
            return "redirect:/crear-cuenta?error=password";
        }

        String contraseniaEncriptada = passwordEncoder.encode(contrasenia);
        Administrador administrador = new Administrador(nombre, correo, contraseniaEncriptada, telefono);
        administradorRepository.save(administrador);

        return "redirect:/admin/login";
    }
}