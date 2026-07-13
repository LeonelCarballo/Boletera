package com.example.ProyectoBoletera.services;

import com.example.ProyectoBoletera.dominio.enums.EstadoBoleto;
import com.example.ProyectoBoletera.dominio.enums.EstadoPago;
import com.example.ProyectoBoletera.dominio.model.*;
import com.example.ProyectoBoletera.dominio.repository.AsientoRepository;
import com.example.ProyectoBoletera.dominio.repository.BoletoClienteRepository;
import com.example.ProyectoBoletera.dominio.repository.BoletoRepository;
import com.example.ProyectoBoletera.dominio.repository.CompraRepository;
import com.example.ProyectoBoletera.dominio.repository.UsuarioRepository;
import com.example.ProyectoBoletera.dto.CompraResponseDTO;
import com.example.ProyectoBoletera.dto.ItemCompraDTO;
import com.example.ProyectoBoletera.dto.TicketDTO;
import com.example.ProyectoBoletera.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class CompraService {

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private BoletoClienteRepository boletoClienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AsientoRepository asientoRepository;

    @Transactional
    public CompraResponseDTO realizarCompra(String emailCliente, List<ItemCompraDTO> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar al menos un boleto.");
        }

        Cliente cliente = obtenerClientePorEmail(emailCliente);

        BigDecimal total = BigDecimal.ZERO;
        List<Boleto> boletosValidados = new ArrayList<>();
        List<List<Asiento>> asientosValidados = new ArrayList<>();

        for (ItemCompraDTO item : items) {
            Boleto boleto = boletoRepository.findById(item.getBoletoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Boleto no encontrado con id " + item.getBoletoId()));

            List<Asiento> asientosDelItem = validarAsientosDelItem(item, boleto);
            if (!asientosDelItem.isEmpty()) {
                item.setCantidad(asientosDelItem.size());
            }

            if (item.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
            }

            if (boleto.getDisponibles() < item.getCantidad()) {
                throw new IllegalArgumentException("No hay suficientes boletos de tipo '" +
                        boleto.getTipo() + "'. Disponibles: " + boleto.getDisponibles());
            }

            total = total.add(boleto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
            boletosValidados.add(boleto);
            asientosValidados.add(asientosDelItem);
        }

        Compra compra = new Compra(LocalDateTime.now(), total, EstadoPago.COMPLETADO, cliente);
        Compra compraGuardada = compraRepository.save(compra);

        List<TicketDTO> tickets = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            ItemCompraDTO item = items.get(i);
            Boleto boleto = boletosValidados.get(i);
            List<Asiento> asientos = asientosValidados.get(i);

            boleto.setDisponibles(boleto.getDisponibles() - item.getCantidad());
            boletoRepository.save(boleto);

            for (int j = 0; j < item.getCantidad(); j++) {
                BoletoCliente boletoCliente = new BoletoCliente(
                        UUID.randomUUID().toString(),
                        EstadoBoleto.DISPONIBLE,
                        compraGuardada,
                        boleto
                );
                if (!asientos.isEmpty()) {
                    boletoCliente.setAsiento(asientos.get(j));
                }
                boletoClienteRepository.save(boletoCliente);

                tickets.add(mapearTicket(boletoCliente, boleto, compraGuardada));
            }
        }

        return new CompraResponseDTO(
                compraGuardada.getId(),
                compraGuardada.getTotal(),
                compraGuardada.getFechaCompra(),
                tickets
        );
    }

    /**
     * Valida los asientos elegidos para un ítem. Si el boleto tiene zona numerada,
     * los asientos son obligatorios, deben pertenecer a esa zona y estar libres.
     * Si el boleto se vende por cantidad, no se aceptan asientos.
     */
    private List<Asiento> validarAsientosDelItem(ItemCompraDTO item, Boleto boleto) {
        boolean traeAsientos = item.getAsientos() != null && !item.getAsientos().isEmpty();

        if (boleto.getZona() == null) {
            if (traeAsientos) {
                throw new IllegalArgumentException("El boleto '" + boleto.getTipo() +
                        "' no maneja asientos numerados.");
            }
            return new ArrayList<>();
        }

        if (!traeAsientos) {
            throw new IllegalArgumentException("Debes elegir tus asientos para el boleto '" +
                    boleto.getTipo() + "'.");
        }

        Set<Long> idsUnicos = new LinkedHashSet<>(item.getAsientos());
        if (idsUnicos.size() != item.getAsientos().size()) {
            throw new IllegalArgumentException("Hay asientos repetidos en tu selección.");
        }

        List<Asiento> asientos = new ArrayList<>();
        for (Long asientoId : idsUnicos) {
            Asiento asiento = asientoRepository.findById(asientoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Asiento no encontrado con id " + asientoId));

            if (!asiento.getZona().getId().equals(boleto.getZona().getId())) {
                throw new IllegalArgumentException("El asiento " + asiento.getEtiqueta() +
                        " no pertenece a la zona del boleto '" + boleto.getTipo() + "'.");
            }

            if (boletoClienteRepository.existsByBoletoIdAndAsientoId(boleto.getId(), asiento.getId())) {
                throw new IllegalArgumentException("El asiento " + asiento.getEtiqueta() +
                        " ya fue vendido. Elige otro.");
            }

            asientos.add(asiento);
        }

        return asientos;
    }

    public List<TicketDTO> obtenerMisTickets(String emailCliente) {
        Cliente cliente = obtenerClientePorEmail(emailCliente);

        List<Compra> compras = compraRepository.findByClienteId(cliente.getId());
        List<TicketDTO> tickets = new ArrayList<>();

        for (Compra compra : compras) {
            List<BoletoCliente> boletosCliente = boletoClienteRepository.findByCompraId(compra.getId());
            for (BoletoCliente bc : boletosCliente) {
                tickets.add(mapearTicket(bc, bc.getBoleto(), compra));
            }
        }

        return tickets;
    }

    private TicketDTO mapearTicket(BoletoCliente boletoCliente, Boleto boleto, Compra compra) {
        Evento evento = boleto.getEvento();
        Lugar lugar = evento != null ? evento.getLugar() : null;
        Asiento asiento = boletoCliente.getAsiento();

        return new TicketDTO(
                boletoCliente.getId(),
                boletoCliente.getCodigoQr(),
                boletoCliente.getEstado().name(),
                boleto.getTipo(),
                boleto.getPrecio(),
                evento != null ? evento.getId() : null,
                evento != null ? evento.getNombre() : null,
                evento != null ? evento.getFecha() : null,
                lugar != null ? lugar.getNombre() : null,
                lugar != null ? lugar.getCiudad() : null,
                compra.getFechaCompra(),
                boleto.getZona() != null ? boleto.getZona().getNombre() : null,
                asiento != null ? asiento.getEtiqueta() : null
        );
    }

    private Cliente obtenerClientePorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        if (!(usuario instanceof Cliente)) {
            throw new IllegalArgumentException("Solo los clientes pueden realizar compras.");
        }

        return (Cliente) usuario;
    }
}
