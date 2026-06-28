package com.example.ProyectoBoletera.services;

import com.example.ProyectoBoletera.dominio.model.Evento;
import com.example.ProyectoBoletera.dominio.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public List<Evento> obtenerTodos() {
        return eventoRepository.findAll();
    }

    public Evento obtenerPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con id " + id));
    }

    public Evento crearEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Evento actualizarEventoCompleto(Long id, Evento datosNuevos) {
        Evento evento = obtenerPorId(id);
        datosNuevos.setId(evento.getId());
        return eventoRepository.save(datosNuevos);
    }

    public Evento actualizarEventoParcial(Long id, Evento datosParciales) {
        Evento evento = obtenerPorId(id);

        if (datosParciales.getNombre() != null) evento.setNombre(datosParciales.getNombre());
        if (datosParciales.getDescripcion() != null) evento.setDescripcion(datosParciales.getDescripcion());
        if (datosParciales.getFecha() != null) evento.setFecha(datosParciales.getFecha());
        if (datosParciales.getImagenUrl() != null) evento.setImagenUrl(datosParciales.getImagenUrl());
        if (datosParciales.getEstado() != null) evento.setEstado(datosParciales.getEstado());

        return eventoRepository.save(evento);
    }

    public void eliminarEvento(Long id) {
        eventoRepository.deleteById(id);
    }
}
