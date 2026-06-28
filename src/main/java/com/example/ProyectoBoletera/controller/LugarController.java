package com.example.ProyectoBoletera.controller;

import com.example.ProyectoBoletera.dominio.model.Lugar;
import com.example.ProyectoBoletera.services.LugarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lugares")
public class LugarController {

    @Autowired
    private LugarService lugarService;

    @GetMapping
    public List<Lugar> obtenerTodos() {
        return lugarService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Lugar obtenerPorId(@PathVariable Long id) {
        return lugarService.obtenerPorId(id);
    }

    @PostMapping
    public Lugar crearLugar(@Valid @RequestBody Lugar lugar) {
        return lugarService.crearLugar(lugar);
    }

    @PutMapping("/{id}")
    public Lugar editarLugar(@PathVariable Long id, @Valid @RequestBody Lugar lugar) {
        return lugarService.actualizarLugarCompleto(id, lugar);
    }

    @PatchMapping("/{id}")
    public Lugar editarLugarParcial(@PathVariable Long id, @RequestBody Lugar lugar) {
        return lugarService.actualizarLugarParcial(id, lugar);
    }

    @DeleteMapping("/{id}")
    public void eliminarLugar(@PathVariable Long id) {
        lugarService.eliminarLugar(id);
    }
}