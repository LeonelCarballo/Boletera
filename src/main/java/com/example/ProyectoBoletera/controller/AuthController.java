package com.example.ProyectoBoletera.controller;

import com.example.ProyectoBoletera.dominio.model.Usuario;
import com.example.ProyectoBoletera.dominio.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping({"/iniciar-sesion", "/iniciar-sesion.html"})
    public String mostrarLogin() {
        return "iniciar-sesion";
    }

    @PostMapping("/iniciar-sesion")
    public String iniciarSesion(@RequestParam String correo,
                                @RequestParam String contrasenia,
                                HttpServletRequest request,
                                Model model) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(correo);

        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "iniciar-sesion";
        }

        Usuario usuario = usuarioOpt.get();
        boolean coincide;

        // Compatibilidad: si la contraseña ya está encriptada, usa BCrypt.
        // Si no, compara texto plano (solo temporal, para datos viejos de prueba).
        if (usuario.getContrasenia().startsWith("$2a$") || usuario.getContrasenia().startsWith("$2b$")) {
            coincide = passwordEncoder.matches(contrasenia, usuario.getContrasenia());
        } else {
            coincide = usuario.getContrasenia().equals(contrasenia);
        }

        if (!coincide) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "iniciar-sesion";
        }

        HttpSession session = request.getSession();
        session.setAttribute("usuario_id", usuario.getId());
        session.setAttribute("usuario_rol", usuario.getRol().name());
        session.setAttribute("usuario_nombre", usuario.getNombre());

        if ("ADMIN".equals(usuario.getRol().name())) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/";
    }

    @PostMapping("/admin/logout")
    public String cerrarSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/iniciar-sesion";
    }
}