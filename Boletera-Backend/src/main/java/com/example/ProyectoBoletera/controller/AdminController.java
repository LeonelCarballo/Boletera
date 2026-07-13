package com.example.ProyectoBoletera.controller;

import com.example.ProyectoBoletera.dominio.enums.CategoriaEvento;
import com.example.ProyectoBoletera.dominio.model.Boleto;
import com.example.ProyectoBoletera.dominio.model.Evento;
import com.example.ProyectoBoletera.dominio.model.Lugar;
import com.example.ProyectoBoletera.dominio.model.Zona;
import com.example.ProyectoBoletera.services.EventoService;
import com.example.ProyectoBoletera.services.LugarService;
import com.example.ProyectoBoletera.services.UsuarioService;
import com.example.ProyectoBoletera.services.ZonaService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EventoService eventoService;

    @Autowired
    private LugarService lugarService;

    @Autowired
    private ZonaService zonaService;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model, HttpSession session) {
        model.addAttribute("totalUsuarios", usuarioService.contarTodos());
        model.addAttribute("totalEventos", eventoService.contarTodos());
        model.addAttribute("totalEventosActivos", eventoService.contarActivos());
        model.addAttribute("totalEventosInactivos", eventoService.contarInactivos());
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

    @GetMapping("/admin/eventos/nuevo")
    public String mostrarFormularioEvento(Model model) {
        model.addAttribute("lugares", lugarService.obtenerTodos());
        model.addAttribute("zonasData", zonasParaFormulario());
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
                              @RequestParam int cantidadGeneral,
                              @RequestParam BigDecimal precioGeneral,
                              @RequestParam(required = false) Long zonaGeneralId,
                              @RequestParam int cantidadPreferente,
                              @RequestParam BigDecimal precioPreferente,
                              @RequestParam(required = false) Long zonaPreferenteId,
                              @RequestParam int cantidadVip,
                              @RequestParam BigDecimal precioVip,
                              @RequestParam(required = false) Long zonaVipId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {

        try {
            String correoAdmin = authentication.getName();
            eventoService.crearEventoDesdeFormulario(nombre, descripcion, fecha, capacidadTotal,
                    categoria, lugarId, correoAdmin, imagen,
                    cantidadGeneral, precioGeneral, zonaGeneralId,
                    cantidadPreferente, precioPreferente, zonaPreferenteId,
                    cantidadVip, precioVip, zonaVipId);

            return "redirect:/admin/eventos";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/eventos/nuevo";
        }
    }

    @GetMapping("/admin/eventos/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Evento evento = eventoService.obtenerPorId(id);
        List<Boleto> boletos = eventoService.obtenerBoletosPorEvento(id);

        model.addAttribute("evento", evento);
        model.addAttribute("lugares", lugarService.obtenerTodos());
        model.addAttribute("zonasData", zonasParaFormulario());
        model.addAttribute("boletoGeneral", boletos.stream()
                .filter(b -> "General".equals(b.getTipo())).findFirst().orElse(null));
        model.addAttribute("boletoPreferente", boletos.stream()
                .filter(b -> "Preferente".equals(b.getTipo())).findFirst().orElse(null));
        model.addAttribute("boletoVip", boletos.stream()
                .filter(b -> "VIP".equals(b.getTipo())).findFirst().orElse(null));

        return "admin/eventos/formulario";
    }

    // Zonas de todos los lugares en formato simple para el JS del formulario de eventos
    private List<Map<String, Object>> zonasParaFormulario() {
        return zonaService.obtenerTodas().stream()
                .map(zona -> {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("id", zona.getId());
                    datos.put("nombre", zona.getNombre());
                    datos.put("lugarId", zona.getLugar().getId());
                    datos.put("capacidad", zona.getCapacidad());
                    datos.put("filas", zona.getFilas());
                    datos.put("asientosPorFila", zona.getAsientosPorFila());
                    return datos;
                })
                .toList();
    }

    @PostMapping("/admin/eventos/{id}")
    public String actualizarEvento(@PathVariable Long id,
                                   @RequestParam String nombre,
                                   @RequestParam String descripcion,
                                   @RequestParam String fecha,
                                   @RequestParam int capacidadTotal,
                                   @RequestParam CategoriaEvento categoria,
                                   @RequestParam Long lugarId,
                                   @RequestParam(value = "imagen", required = false) MultipartFile imagen,
                                   @RequestParam int cantidadGeneral,
                                   @RequestParam BigDecimal precioGeneral,
                                   @RequestParam(required = false) Long zonaGeneralId,
                                   @RequestParam int cantidadPreferente,
                                   @RequestParam BigDecimal precioPreferente,
                                   @RequestParam(required = false) Long zonaPreferenteId,
                                   @RequestParam int cantidadVip,
                                   @RequestParam BigDecimal precioVip,
                                   @RequestParam(required = false) Long zonaVipId,
                                   RedirectAttributes redirectAttributes) {

        try {
            eventoService.actualizarEventoDesdeFormulario(id, nombre, descripcion, fecha, capacidadTotal,
                    categoria, lugarId, imagen,
                    cantidadGeneral, precioGeneral, zonaGeneralId,
                    cantidadPreferente, precioPreferente, zonaPreferenteId,
                    cantidadVip, precioVip, zonaVipId);

            return "redirect:/admin/eventos";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/eventos/" + id + "/editar";
        }
    }

    @PostMapping("/admin/eventos/{id}/eliminar")
    public String eliminarEvento(@PathVariable Long id) {
        eventoService.eliminarEvento(id);
        return "redirect:/admin/eventos";
    }

    @PostMapping("/admin/eventos/{id}/alternar-estado")
    public String alternarEstadoEvento(@PathVariable Long id) {
        eventoService.alternarEstadoInactivo(id);
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

    @GetMapping("/admin/lugares/{id}/editar")
    public String mostrarFormularioEditarLugar(@PathVariable Long id, Model model) {
        model.addAttribute("lugar", lugarService.obtenerPorId(id));
        return "admin/lugares/formulario";
    }

    @PostMapping("/admin/lugares/{id}")
    public String actualizarLugar(@PathVariable Long id,
                                  @RequestParam String nombre,
                                  @RequestParam String direccion,
                                  @RequestParam String ciudad,
                                  @RequestParam String estado,
                                  @RequestParam Integer capacidad,
                                  RedirectAttributes redirectAttributes) {

        try {
            Lugar datosNuevos = new Lugar(nombre, direccion, ciudad, estado, capacidad);
            lugarService.actualizarLugar(id, datosNuevos);
            return "redirect:/admin/lugares";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/lugares/" + id + "/editar";
        }
    }

    @PostMapping("/admin/lugares/{id}/eliminar")
    public String eliminarLugar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            lugarService.eliminarLugar(id);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/lugares";
    }

    // Gestión de zonas (asientos numerados) por lugar

    @GetMapping("/admin/lugares/{id}/zonas")
    public String zonasDeLugar(@PathVariable Long id, Model model) {
        Lugar lugar = lugarService.obtenerPorId(id);
        List<Zona> zonas = zonaService.obtenerPorLugar(id);
        int capacidadEnZonas = zonas.stream().mapToInt(Zona::getCapacidad).sum();

        model.addAttribute("lugar", lugar);
        model.addAttribute("zonas", zonas);
        model.addAttribute("capacidadEnZonas", capacidadEnZonas);
        model.addAttribute("currentPage", "lugares");
        return "admin/lugares/zonas";
    }

    @PostMapping("/admin/lugares/{id}/zonas")
    public String crearZona(@PathVariable Long id,
                            @RequestParam String nombre,
                            @RequestParam int filas,
                            @RequestParam int asientosPorFila,
                            RedirectAttributes redirectAttributes) {
        try {
            zonaService.crearZona(id, nombre, filas, asientosPorFila);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/lugares/" + id + "/zonas";
    }

    @PostMapping("/admin/lugares/{id}/zonas/{zonaId}/eliminar")
    public String eliminarZona(@PathVariable Long id,
                               @PathVariable Long zonaId,
                               RedirectAttributes redirectAttributes) {
        try {
            zonaService.eliminarZona(zonaId);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/lugares/" + id + "/zonas";
    }

    @GetMapping("/admin/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.obtenerTodos());
        model.addAttribute("currentPage", "usuarios");
        return "admin/usuarios/listar";
    }

    //Gestion de Usuarios desde el Panel Admin

    @GetMapping("/admin/usuarios/{id}/editar")
    public String mostrarFormularioEditarUsuario(@PathVariable Long id, Model model) {
        // Buscamos el usuario por ID y lo mandamos al formulario
        model.addAttribute("usuario", usuarioService.obtenerPorId(id));
        return "admin/usuarios/formulario";
    }

    @PostMapping("/admin/usuarios/{id}")
    public String actualizarUsuario(@PathVariable Long id,
                                    @RequestParam String nombre,
                                    @RequestParam String email,
                                    @RequestParam(required = false) String contrasenia,
                                    @RequestParam String telefono,
                                    RedirectAttributes redirectAttributes) {
        try {
            usuarioService.actualizarUsuarioDesdeFormulario(id, nombre, email, contrasenia, telefono);

            return "redirect:/admin/usuarios";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuarios/" + id + "/editar";
        }
    }

    @PostMapping("/admin/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminar(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar al usuario porque tiene dependencias activas.");
        }
        return "redirect:/admin/usuarios";
    }
}