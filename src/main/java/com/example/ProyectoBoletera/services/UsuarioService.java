package com.example.ProyectoBoletera.services;

import com.example.ProyectoBoletera.dominio.model.Usuario;
import com.example.ProyectoBoletera.dominio.repository.UsuarioRepository;
import com.example.ProyectoBoletera.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con el id " + id));
    }

    public Usuario actualizarParcial(Long id, Map<String, String> cambios) {
        return usuarioRepository.findById(id).map(usuario -> {
            if (cambios.containsKey("nombre")) usuario.setNombre(cambios.get("nombre"));
            if (cambios.containsKey("email")) usuario.setEmail(cambios.get("email"));
            if (cambios.containsKey("telefono")) usuario.setTelefono(cambios.get("telefono"));
            return usuarioRepository.save(usuario);
        }).orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario para editarlo"));
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("El usuario no se ha encontrado, no se puede eliminar");
        }
        usuarioRepository.deleteById(id);
    }

    public long contarTodos() {
        return usuarioRepository.count();
    }
}