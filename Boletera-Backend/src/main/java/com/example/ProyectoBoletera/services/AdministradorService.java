package com.example.ProyectoBoletera.services;

import com.example.ProyectoBoletera.dominio.model.Administrador;
import com.example.ProyectoBoletera.dominio.repository.AdministradorRepository;
import com.example.ProyectoBoletera.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Administrador> obtenerTodos() {
        return administradorRepository.findAll();
    }

    public Administrador obtenerPorId(Long id) {
        return administradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el administrador con el id " + id));
    }

    public Administrador crear(Administrador administrador) {
        administrador.setContrasenia(passwordEncoder.encode(administrador.getContrasenia()));
        return administradorRepository.save(administrador);
    }
}