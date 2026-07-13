package com.example.ProyectoBoletera.services;

import com.example.ProyectoBoletera.dominio.model.Asiento;
import com.example.ProyectoBoletera.dominio.model.Lugar;
import com.example.ProyectoBoletera.dominio.model.Zona;
import com.example.ProyectoBoletera.dominio.repository.AsientoRepository;
import com.example.ProyectoBoletera.dominio.repository.BoletoRepository;
import com.example.ProyectoBoletera.dominio.repository.LugarRepository;
import com.example.ProyectoBoletera.dominio.repository.ZonaRepository;
import com.example.ProyectoBoletera.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ZonaService {

    private static final int MAX_FILAS = 26; // filas nombradas A-Z
    private static final int MAX_ASIENTOS_POR_FILA = 50;

    @Autowired
    private ZonaRepository zonaRepository;

    @Autowired
    private AsientoRepository asientoRepository;

    @Autowired
    private LugarRepository lugarRepository;

    @Autowired
    private BoletoRepository boletoRepository;

    public List<Zona> obtenerPorLugar(Long lugarId) {
        return zonaRepository.findByLugarId(lugarId);
    }

    public List<Zona> obtenerTodas() {
        return zonaRepository.findAll();
    }

    public Zona obtenerPorId(Long id) {
        return zonaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zona no encontrada con id " + id));
    }

    @Transactional
    public Zona crearZona(Long lugarId, String nombre, int filas, int asientosPorFila) {
        Lugar lugar = lugarRepository.findById(lugarId)
                .orElseThrow(() -> new ResourceNotFoundException("Lugar no encontrado con id " + lugarId));

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la zona es obligatorio.");
        }
        if (filas < 1 || filas > MAX_FILAS) {
            throw new IllegalArgumentException("Las filas deben estar entre 1 y " + MAX_FILAS + " (una letra por fila).");
        }
        if (asientosPorFila < 1 || asientosPorFila > MAX_ASIENTOS_POR_FILA) {
            throw new IllegalArgumentException("Los asientos por fila deben estar entre 1 y " + MAX_ASIENTOS_POR_FILA + ".");
        }
        if (zonaRepository.existsByLugarIdAndNombreIgnoreCase(lugarId, nombre.trim())) {
            throw new IllegalArgumentException("Ya existe una zona llamada '" + nombre.trim() + "' en este lugar.");
        }

        int capacidadNueva = filas * asientosPorFila;
        int capacidadOcupada = zonaRepository.findByLugarId(lugarId).stream()
                .mapToInt(Zona::getCapacidad)
                .sum();

        if (capacidadOcupada + capacidadNueva > lugar.getCapacidad()) {
            throw new IllegalArgumentException("La zona no cabe en el lugar: las zonas existentes suman " +
                    capacidadOcupada + " asientos y la capacidad del lugar es " + lugar.getCapacidad() + ".");
        }

        Zona zona = zonaRepository.save(new Zona(nombre.trim(), filas, asientosPorFila, lugar));

        // Genera los asientos físicos de la zona
        List<Asiento> asientos = new ArrayList<>();
        for (int f = 0; f < filas; f++) {
            String letraFila = String.valueOf((char) ('A' + f));
            for (int n = 1; n <= asientosPorFila; n++) {
                asientos.add(new Asiento(letraFila, n, zona));
            }
        }
        asientoRepository.saveAll(asientos);

        return zona;
    }

    @Transactional
    public void eliminarZona(Long zonaId) {
        Zona zona = obtenerPorId(zonaId);

        if (boletoRepository.existsByZonaId(zonaId)) {
            throw new IllegalArgumentException("No se puede eliminar la zona '" + zona.getNombre() +
                    "' porque está asignada a boletos de algún evento.");
        }

        asientoRepository.deleteByZonaId(zonaId);
        zonaRepository.delete(zona);
    }
}
