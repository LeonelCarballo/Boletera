package com.example.ProyectoBoletera.services;

import com.example.ProyectoBoletera.dominio.model.Lugar;
import com.example.ProyectoBoletera.dominio.repository.LugarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LugarService {

    @Autowired
    private LugarRepository lugarRepository;

    public List<Lugar> obtenerTodos() {
        return lugarRepository.findAll();
    }

    public Lugar obtenerPorId(Long id) {
        return lugarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lugar no encontrado con id " + id));
    }

    public Lugar crearLugar(Lugar lugar) {
        return lugarRepository.save(lugar);
    }

    public Lugar actualizarLugarCompleto(Long id, Lugar datosNuevos) {
        Lugar lugar = obtenerPorId(id);
        datosNuevos.setId(lugar.getId());
        return lugarRepository.save(datosNuevos);
    }

    public Lugar actualizarLugarParcial(Long id, Lugar datosParciales) {
        Lugar lugar = obtenerPorId(id);

        if (datosParciales.getNombre() != null) lugar.setNombre(datosParciales.getNombre());
        if (datosParciales.getDireccion() != null) lugar.setDireccion(datosParciales.getDireccion());
        if (datosParciales.getCiudad() != null) lugar.setCiudad(datosParciales.getCiudad());
        if (datosParciales.getEstado() != null) lugar.setEstado(datosParciales.getEstado());
        if (datosParciales.getCapacidad() != null) lugar.setCapacidad(datosParciales.getCapacidad());

        return lugarRepository.save(lugar);
    }

    public void eliminarLugar(Long id) {
        lugarRepository.deleteById(id);
    }
}
