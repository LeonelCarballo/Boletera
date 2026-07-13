package com.example.ProyectoBoletera.controller;

import com.example.ProyectoBoletera.dominio.model.Cliente;
import com.example.ProyectoBoletera.services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public List<Cliente> obtenerTodos() {
        return clienteService.obtenerTodos();
    }

    @PostMapping
    public Cliente crearCliente(@Valid @RequestBody Cliente cliente) {
        return clienteService.crearCliente(cliente);
    }

    @PutMapping("/{id}")
    public Cliente editarCliente(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        return clienteService.actualizarClienteCompleto(id, cliente);
    }
}