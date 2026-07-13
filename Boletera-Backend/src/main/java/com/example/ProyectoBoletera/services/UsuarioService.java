package com.example.ProyectoBoletera.services;

import com.example.ProyectoBoletera.dominio.model.Usuario;
import com.example.ProyectoBoletera.dominio.repository.UsuarioRepository;
import com.example.ProyectoBoletera.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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


    /**
     * Actualiza un usuario desde el formulario de administración web.
     */
    public void actualizarUsuarioDesdeFormulario(Long id, String nombre, String email, String contrasenia, String telefono) {
        // 1. Validar que el usuario exista
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con el id " + id));

        // 2. Actualizar los campos obligatorios
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setTelefono(telefono);


        // 3. Lógica para la contraseña: solo se cambia si el administrador escribió algo
        if (contrasenia != null && !contrasenia.trim().isEmpty()) {
            // Si usas PasswordEncoder (recomendado), descomenta la línea de abajo y borra la asignación directa:
//             usuario.setContrasenia(passwordEncoder.encode(contrasenia));
            usuario.setContrasenia(contrasenia);
        }

        // 4. Guardar los cambios (JPA se encarga de actualizar tanto la tabla Usuario como la hija Cliente/Admin)
        usuarioRepository.save(usuario);
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

    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public Usuario actualizarPerfil(String email, String nombre, String telefono) {
        Usuario usuario = obtenerPorEmail(email);

        if (nombre != null && !nombre.isBlank()) {
            if (nombre.trim().length() < 3) {
                throw new IllegalArgumentException("El nombre debe tener al menos 3 caracteres.");
            }
            usuario.setNombre(nombre.trim());
        }

        if (telefono != null && !telefono.isBlank()) {
            if (!telefono.matches("\\d{10}")) {
                throw new IllegalArgumentException("El teléfono debe tener exactamente 10 dígitos.");
            }
            usuario.setTelefono(telefono);
        }

        return usuarioRepository.save(usuario);
    }

    public void cambiarContrasenia(String email, String contraseniaActual, String contraseniaNueva) {
        Usuario usuario = obtenerPorEmail(email);

        if (contraseniaActual == null
                || !passwordEncoder.matches(contraseniaActual, usuario.getContrasenia())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta.");
        }

        if (contraseniaNueva == null || contraseniaNueva.length() < 8) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 8 caracteres.");
        }

        usuario.setContrasenia(passwordEncoder.encode(contraseniaNueva));
        usuarioRepository.save(usuario);
    }
}