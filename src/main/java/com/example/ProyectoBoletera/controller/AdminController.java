package com.example.ProyectoBoletera.controller;

import com.example.ProyectoBoletera.dominio.enums.CategoriaEvento;
import com.example.ProyectoBoletera.dominio.model.Lugar;
import com.example.ProyectoBoletera.services.EventoService;
import com.example.ProyectoBoletera.services.LugarService;
import com.example.ProyectoBoletera.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EventoService eventoService;

    @Autowired
    private LugarService lugarService;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model, HttpSession session) {
        model.addAttribute("totalUsuarios", usuarioService.contarTodos());
        model.addAttribute("totalEventos", eventoService.contarTodos());
        model.addAttribute("totalLugares", lugarService.contarTodos());
        model.addAttribute("currentPage", "dashboard");
        model.addAttribute("nombreUsuario", session.getAttribute("usuario_nombre"));

        return "admin/dashboard";
    }

    @GetMapping("/admin/eventos")
    public String eventos(Model model) {
        model.addAttribute("eventos", eventoService.obtenerTodos());
        model.addAttribute("currentPage", "eventos");
        return "admin/eventos/listar";
    }

    @PostMapping("/admin/eventos/{id}/eliminar")
    public String eliminarEvento(@PathVariable Long id) {
        eventoService.eliminarEvento(id);
        return "redirect:/admin/eventos";
    }

    @GetMapping("/admin/eventos/nuevo")
    public String mostrarFormularioEvento(Model model) {
        model.addAttribute("lugares", lugarService.obtenerTodos());
        return "admin/eventos/formulario";
    }

    @PostMapping("/admin/eventos")
    public String crearEvento(@RequestParam String nombre,
                              @RequestParam String descripcion,
                              @RequestParam String fecha,
                              @RequestParam int capacidadTotal,
                              @RequestParam CategoriaEvento categoria,
                              @RequestParam Long lugarId,
                              @RequestParam("imagen") MultipartFile imagen,
                              Authentication authentication) {

        String correoAdmin = authentication.getName();
        eventoService.crearEventoDesdeFormulario(nombre, descripcion, fecha, capacidadTotal,
                categoria, lugarId, correoAdmin, imagen);

        return "redirect:/admin/eventos";
    }

    @GetMapping("/admin/lugares")
    public String lugares(Model model) {
        model.addAttribute("lugares", lugarService.obtenerTodos());
        model.addAttribute("currentPage", "lugares");
        return "admin/lugares/listar";
    }

    @GetMapping("/admin/lugares/nuevo")
    public String mostrarFormularioLugar() {
        return "admin/lugares/formulario";
    }

    @PostMapping("/admin/lugares")
    public String crearLugar(@RequestParam String nombre,
                             @RequestParam String direccion,
                             @RequestParam String ciudad,
                             @RequestParam String estado,
                             @RequestParam Integer capacidad) {

        Lugar lugar = new Lugar(nombre, direccion, ciudad, estado, capacidad);
        lugarService.crearLugar(lugar);

        return "redirect:/admin/lugares";
    }

    @GetMapping("/admin/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.obtenerTodos());
        model.addAttribute("currentPage", "usuarios");
        return "admin/usuarios/listar";
    }
}