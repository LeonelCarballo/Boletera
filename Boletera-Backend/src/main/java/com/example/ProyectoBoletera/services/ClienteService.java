package com.example.ProyectoBoletera.services;

import com.example.ProyectoBoletera.dominio.model.Cliente;
import com.example.ProyectoBoletera.dominio.repository.ClienteRepository;
import com.example.ProyectoBoletera.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el cliente con el id " + id));
    }

    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente actualizarClienteCompleto(Long id, Cliente datosNuevos) {
        return clienteRepository.findById(id).map(cliente -> {
            datosNuevos.setId(cliente.getId());
            return clienteRepository.save(datosNuevos);
        }).orElseThrow(() -> new ResourceNotFoundException("No se encontró el cliente para editarlo"));
    }
}