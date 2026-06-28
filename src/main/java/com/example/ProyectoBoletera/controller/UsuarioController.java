package com.example.ProyectoBoletera.controller;

import com.example.ProyectoBoletera.dominio.model.Usuario;
import com.example.ProyectoBoletera.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> obtenerTodos() {
        return usuarioService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Usuario obtenerPorId(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id);
    }

    @PatchMapping("/{id}")
    public Usuario editarParcial(@PathVariable Long id, @RequestBody Map<String, String> cambios) {
        return usuarioService.actualizarParcial(id, cambios);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
    }
}