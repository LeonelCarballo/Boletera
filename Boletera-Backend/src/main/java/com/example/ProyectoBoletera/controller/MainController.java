package com.example.ProyectoBoletera.controller;

import com.example.ProyectoBoletera.dominio.repository.UsuarioRepository;
import com.example.ProyectoBoletera.services.EventoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import com.example.ProyectoBoletera.dominio.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

@Controller
public class MainController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoService eventoService;

    @GetMapping({"/", ""})
    public String home(){
        return "redirect:/index";
    }

    @GetMapping({"/index", "/index.html"})
    public String index(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            usuarioRepository.findByEmail(authentication.getName())
                    .ifPresent(usuario -> model.addAttribute("nombreUsuario", usuario.getNombre()));
        }
        model.addAttribute("eventos", eventoService.obtenerTodos()
                .stream()
                .limit(4)
                .toList());
        return "index";
    }

    @GetMapping({"/crear-cuenta", "/crear-cuenta.html"})
    public String crearCuenta(){
        return "crear-cuenta";
    }

    @GetMapping("/eventos")
    public String eventos(Model model, Authentication authentication) {
        agregarNombreUsuario(model, authentication);
        model.addAttribute("eventos", eventoService.obtenerTodos());
        return "eventos";
    }

    private void agregarNombreUsuario(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            usuarioRepository.findByEmail(authentication.getName())
                    .ifPresent(usuario -> model.addAttribute("nombreUsuario", usuario.getNombre()));
        }
    }
}
