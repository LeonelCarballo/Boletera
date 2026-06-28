package com.example.ProyectoBoletera.controller;

import com.example.ProyectoBoletera.dominio.enums.CategoriaEvento;
import com.example.ProyectoBoletera.dominio.enums.EstadoEvento;
import com.example.ProyectoBoletera.dominio.model.Administrador;
import com.example.ProyectoBoletera.dominio.model.Evento;
import com.example.ProyectoBoletera.dominio.model.Lugar;
import com.example.ProyectoBoletera.dominio.repository.AdministradorRepository;
import com.example.ProyectoBoletera.dominio.repository.EventoRepository;
import com.example.ProyectoBoletera.dominio.repository.LugarRepository;
import com.example.ProyectoBoletera.dominio.repository.UsuarioRepository;
import com.example.ProyectoBoletera.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private LugarRepository lugarRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model, HttpSession session) {
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        model.addAttribute("totalEventos", eventoRepository.count());
        model.addAttribute("totalLugares", lugarRepository.count());
        model.addAttribute("currentPage", "dashboard");
        model.addAttribute("nombreUsuario", session.getAttribute("usuario_nombre"));

        return "admin/dashboard";
    }

    @GetMapping("/admin/eventos")
    public String eventos(Model model) {
        model.addAttribute("eventos", eventoRepository.findAll());
        model.addAttribute("currentPage", "eventos");
        return "admin/eventos/listar";
    }

    @PostMapping("/admin/eventos/{id}/eliminar")
    public String eliminarEvento(@PathVariable Long id) {
        eventoRepository.deleteById(id);
        return "redirect:/admin/eventos";
    }

    @GetMapping("/admin/lugares")
    public String lugares(Model model) {
        model.addAttribute("lugares", lugarRepository.findAll());
        model.addAttribute("currentPage", "lugares");
        return "admin/lugares/listar";
    }

    @GetMapping("/admin/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("currentPage", "usuarios");
        return "admin/usuarios/listar";
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
                             @RequestParam int capacidad) {

        Lugar lugar = new Lugar(nombre, direccion, ciudad, estado, capacidad);
        lugarRepository.save(lugar);

        return "redirect:/admin/lugares";
    }

    @GetMapping("/admin/eventos/nuevo")
    public String mostrarFormularioEvento(Model model) {
        model.addAttribute("lugares", lugarRepository.findAll());
        return "admin/eventos/formulario";
    }

    @PostMapping("/admin/eventos")
    public String crearEvento(@RequestParam String nombre,
                              @RequestParam String descripcion,
                              @RequestParam String fecha,
                              @RequestParam int capacidadTotal,
                              @RequestParam CategoriaEvento categoria,
                              @RequestParam Long lugarId,
                              HttpSession session) {

        Long administradorId = (Long) session.getAttribute("usuario_id");
        Administrador administrador = administradorRepository.findById(administradorId)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado"));

        Lugar lugar = lugarRepository.findById(lugarId)
                .orElseThrow(() -> new ResourceNotFoundException("Lugar no encontrado"));

        Evento evento = new Evento();
        evento.setNombre(nombre);
        evento.setDescripcion(descripcion);
        evento.setFecha(LocalDateTime.parse(fecha));
        evento.setCapacidadTotal(capacidadTotal);
        evento.setCategoria(categoria);
        evento.setEstado(EstadoEvento.EN_VENTA);
        evento.setAdministrador(administrador);
        evento.setLugar(lugar);

        eventoRepository.save(evento);

        return "redirect:/admin/eventos";
    }
}