package com.example.ProyectoBoletera.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping({"/", ""})
    public String home(){
        return "redirect:/index";
    }

    @GetMapping({"/index", "/index.html"})
    public String index(){
        return "index";
    }

    @GetMapping({"/iniciar-sesion", "/iniciar-sesion.html"})
    public String iniciarSesion(){
        return "iniciar-sesion";
    }

    @GetMapping({"/crear-cuenta", "/crear-cuenta.html"})
    public String crearCuenta(){
        return "crear-cuenta";
    }

    @GetMapping("/eventos")
    public String eventos() {
        return "eventos";
    }
}
