package com.example.ProyectoBoletera.controller;

import com.example.ProyectoBoletera.dto.CompraRequestDTO;
import com.example.ProyectoBoletera.dto.CompraResponseDTO;
import com.example.ProyectoBoletera.dto.TicketDTO;
import com.example.ProyectoBoletera.exception.ResourceNotFoundException;
import com.example.ProyectoBoletera.services.CompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @PostMapping
    public ResponseEntity<?> crearCompra(@RequestBody CompraRequestDTO request,
                                         Authentication authentication) {
        try {
            String email = authentication.getName();
            CompraResponseDTO respuesta = compraService.realizarCompra(email, request.getBoletos());
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException | ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(Map.of("error",
                    "Alguien más acaba de comprar uno de los asientos que elegiste. Recarga la página e inténtalo de nuevo."));
        }
    }

    @GetMapping("/mis-tickets")
    public ResponseEntity<?> misTickets(Authentication authentication) {
        try {
            String email = authentication.getName();
            List<TicketDTO> tickets = compraService.obtenerMisTickets(email);
            return ResponseEntity.ok(tickets);
        } catch (IllegalArgumentException | ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
