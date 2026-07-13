package com.example.ProyectoBoletera.controller;


import com.example.ProyectoBoletera.dominio.model.Evento;
import com.example.ProyectoBoletera.dto.ZonaAsientosDTO;
import com.example.ProyectoBoletera.services.EventoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public List<Evento> obtenerTodos() {
        return eventoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Evento obtenerPorId(@PathVariable Long id) {
        return eventoService.obtenerPorId(id);
    }

    // Mapa de asientos por tipo de boleto
    @GetMapping("/{id}/asientos")
    public List<ZonaAsientosDTO> obtenerAsientos(@PathVariable Long id) {
        return eventoService.obtenerMapaAsientos(id);
    }

    @PostMapping
    public Evento crearEvento(@Valid @RequestBody Evento evento) {
        return eventoService.crearEvento(evento);
    }

    @PutMapping("/{id}")
    public Evento editarEvento(@PathVariable Long id, @Valid @RequestBody Evento evento) {
        return eventoService.actualizarEventoCompleto(id, evento);
    }

    @PatchMapping("/{id}")
    public Evento editarEventoParcial(@PathVariable Long id, @RequestBody Evento evento) {
        return eventoService.actualizarEventoParcial(id, evento);
    }

    @DeleteMapping("/{id}")
    public void eliminarEvento(@PathVariable Long id) {
        eventoService.eliminarEvento(id);
    }
}