package com.example.ProyectoBoletera.controller;

import com.example.ProyectoBoletera.dominio.model.Administrador;
import com.example.ProyectoBoletera.services.AdministradorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administradores")
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    @GetMapping
    public List<Administrador> obtenerTodos() {
        return administradorService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Administrador obtenerPorId(@PathVariable Long id) {
        return administradorService.obtenerPorId(id);
    }

    @PostMapping
    public Administrador crear(@Valid @RequestBody Administrador administrador) {
        return administradorService.crear(administrador);
    }


}