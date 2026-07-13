package com.example.ProyectoBoletera.controller;

import com.example.ProyectoBoletera.dominio.model.Usuario;
import com.example.ProyectoBoletera.dto.ActualizarPerfilRequest;
import com.example.ProyectoBoletera.dto.CambiarContraseniaRequest;
import com.example.ProyectoBoletera.dto.PerfilDTO;
import com.example.ProyectoBoletera.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Gestión de la cuenta del usuario autenticado con jwt
@RestController
@RequestMapping("/api/me")
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public PerfilDTO miPerfil(Authentication authentication) {
        Usuario usuario = usuarioService.obtenerPorEmail(authentication.getName());
        return mapearPerfil(usuario);
    }

    @PutMapping
    public ResponseEntity<?> actualizarPerfil(@RequestBody ActualizarPerfilRequest request,
                                              Authentication authentication) {
        try {
            Usuario usuario = usuarioService.actualizarPerfil(
                    authentication.getName(), request.getNombre(), request.getTelefono());
            return ResponseEntity.ok(mapearPerfil(usuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> cambiarContrasenia(@RequestBody CambiarContraseniaRequest request,
                                                Authentication authentication) {
        try {
            usuarioService.cambiarContrasenia(authentication.getName(),
                    request.getContraseniaActual(), request.getContraseniaNueva());
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private PerfilDTO mapearPerfil(Usuario usuario) {
        return new PerfilDTO(usuario.getNombre(), usuario.getEmail(),
                usuario.getTelefono(), usuario.getRol().name());
    }
}
