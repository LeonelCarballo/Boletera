package com.example.ProyectoBoletera.services;

import com.example.ProyectoBoletera.dominio.model.Lugar;
import com.example.ProyectoBoletera.dominio.repository.LugarRepository;
import com.example.ProyectoBoletera.exception.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Lugar no encontrado con id " + id));
    }

    public Lugar crearLugar(Lugar lugar) {
        return lugarRepository.save(lugar);
    }

    public Lugar actualizarLugar(Long id, Lugar datosNuevos) {
        Lugar lugar = obtenerPorId(id);

        lugar.setNombre(datosNuevos.getNombre());
        lugar.setDireccion(datosNuevos.getDireccion());
        lugar.setCiudad(datosNuevos.getCiudad());
        lugar.setEstado(datosNuevos.getEstado());
        lugar.setCapacidad(datosNuevos.getCapacidad());

        return lugarRepository.save(lugar);
    }

    public void eliminarLugar(Long id) {
        Lugar lugar = obtenerPorId(id);

        if (lugar.getEventos() != null && !lugar.getEventos().isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar el lugar '" + lugar.getNombre() +
                    "' porque tiene " + lugar.getEventos().size() + " evento(s) asociado(s).");
        }

        lugarRepository.deleteById(id);
    }

    public long contarTodos() {
        return lugarRepository.count();
    }
}