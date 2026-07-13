package com.example.ProyectoBoletera.services;

import com.example.ProyectoBoletera.dominio.enums.CategoriaEvento;
import com.example.ProyectoBoletera.dominio.enums.EstadoEvento;
import com.example.ProyectoBoletera.dominio.model.Administrador;
import com.example.ProyectoBoletera.dominio.model.Boleto;
import com.example.ProyectoBoletera.dominio.model.Evento;
import com.example.ProyectoBoletera.dominio.model.Lugar;
import com.example.ProyectoBoletera.dominio.model.Usuario;
import com.example.ProyectoBoletera.dominio.model.Zona;
import com.example.ProyectoBoletera.dominio.repository.AdministradorRepository;
import com.example.ProyectoBoletera.dominio.repository.AsientoRepository;
import com.example.ProyectoBoletera.dominio.repository.BoletoClienteRepository;
import com.example.ProyectoBoletera.dominio.repository.BoletoRepository;
import com.example.ProyectoBoletera.dominio.repository.EventoRepository;
import com.example.ProyectoBoletera.dominio.repository.LugarRepository;
import com.example.ProyectoBoletera.dominio.repository.UsuarioRepository;
import com.example.ProyectoBoletera.dominio.repository.ZonaRepository;
import com.example.ProyectoBoletera.dto.AsientoDTO;
import com.example.ProyectoBoletera.dto.ZonaAsientosDTO;
import com.example.ProyectoBoletera.exception.ResourceNotFoundException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private LugarRepository lugarRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private ZonaRepository zonaRepository;

    @Autowired
    private AsientoRepository asientoRepository;

    @Autowired
    private BoletoClienteRepository boletoClienteRepository;

    public List<Evento> obtenerTodos() {
        return eventoRepository.findAll();
    }

    public Evento obtenerPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id " + id));
    }

    public List<Boleto> obtenerBoletosPorEvento(Long eventoId) {
        return boletoRepository.findByEventoId(eventoId);
    }

    public long contarTodos() {
        return eventoRepository.count();
    }

    public Evento crearEventoDesdeFormulario(String nombre, String descripcion, String fecha,
                                             int capacidadTotal, CategoriaEvento categoria,
                                             Long lugarId, String correoAdmin, MultipartFile imagen,
                                             int cantidadGeneral, BigDecimal precioGeneral, Long zonaGeneralId,
                                             int cantidadPreferente, BigDecimal precioPreferente, Long zonaPreferenteId,
                                             int cantidadVip, BigDecimal precioVip, Long zonaVipId) {

        Usuario usuario = usuarioRepository.findByEmail(correoAdmin)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado"));
        Administrador administrador = (Administrador) usuario;

        Lugar lugar = lugarRepository.findById(lugarId)
                .orElseThrow(() -> new ResourceNotFoundException("Lugar no encontrado"));

        LocalDateTime fechaEvento = LocalDateTime.parse(fecha);
        validarFechaNoPasada(fechaEvento);

        Zona zonaGeneral = resolverZona(zonaGeneralId, lugar);
        Zona zonaPreferente = resolverZona(zonaPreferenteId, lugar);
        Zona zonaVip = resolverZona(zonaVipId, lugar);
        validarZonasDistintas(zonaGeneral, zonaPreferente, zonaVip);

        // la cantidad la dicta la capacidad de la zona
        if (zonaGeneral != null) cantidadGeneral = zonaGeneral.getCapacidad();
        if (zonaPreferente != null) cantidadPreferente = zonaPreferente.getCapacidad();
        if (zonaVip != null) cantidadVip = zonaVip.getCapacidad();

        int totalBoletos = cantidadGeneral + cantidadPreferente + cantidadVip;
        if (totalBoletos > lugar.getCapacidad()) {
            throw new IllegalArgumentException("La suma de boletos (" + totalBoletos +
                    ") supera la capacidad del lugar (" + lugar.getCapacidad() + ")");
        }

        Evento evento = new Evento();
        evento.setNombre(nombre);
        evento.setDescripcion(descripcion);
        evento.setFecha(fechaEvento);
        evento.setCapacidadTotal(capacidadTotal);
        evento.setCategoria(categoria);
        evento.setEstado(EstadoEvento.EN_VENTA);
        evento.setAdministrador(administrador);
        evento.setLugar(lugar);

        if (imagen != null && !imagen.isEmpty()) {
            evento.setImagenUrl(subirImagenCloudinary(imagen));
        }

        Evento eventoGuardado = eventoRepository.save(evento);

        crearBoletoSiAplica("General", precioGeneral, cantidadGeneral, zonaGeneral, eventoGuardado);
        crearBoletoSiAplica("Preferente", precioPreferente, cantidadPreferente, zonaPreferente, eventoGuardado);
        crearBoletoSiAplica("VIP", precioVip, cantidadVip, zonaVip, eventoGuardado);

        return eventoGuardado;
    }

    private Zona resolverZona(Long zonaId, Lugar lugar) {
        if (zonaId == null) return null;

        Zona zona = zonaRepository.findById(zonaId)
                .orElseThrow(() -> new ResourceNotFoundException("Zona no encontrada con id " + zonaId));

        if (!zona.getLugar().getId().equals(lugar.getId())) {
            throw new IllegalArgumentException("La zona '" + zona.getNombre() +
                    "' no pertenece al lugar '" + lugar.getNombre() + "'.");
        }

        return zona;
    }

    private void validarZonasDistintas(Zona... zonas) {
        Set<Long> vistas = new HashSet<>();
        for (Zona zona : zonas) {
            if (zona != null && !vistas.add(zona.getId())) {
                throw new IllegalArgumentException("No puedes asignar la zona '" + zona.getNombre() +
                        "' a dos tipos de boleto del mismo evento.");
            }
        }
    }

    private void crearBoletoSiAplica(String tipo, BigDecimal precio, int cantidad, Zona zona, Evento evento) {
        if (cantidad <= 0) return;

        Boleto boleto = new Boleto();
        boleto.setTipo(tipo);
        boleto.setPrecio(precio);
        boleto.setCapacidadMax(cantidad);
        boleto.setDisponibles(cantidad);
        boleto.setZona(zona);
        boleto.setEvento(evento);

        boletoRepository.save(boleto);
    }

    public Evento actualizarEventoDesdeFormulario(Long id, String nombre, String descripcion, String fecha,
                                                  int capacidadTotal, CategoriaEvento categoria,
                                                  Long lugarId, MultipartFile imagen,
                                                  int cantidadGeneral, BigDecimal precioGeneral, Long zonaGeneralId,
                                                  int cantidadPreferente, BigDecimal precioPreferente, Long zonaPreferenteId,
                                                  int cantidadVip, BigDecimal precioVip, Long zonaVipId) {

        Evento evento = obtenerPorId(id);

        Lugar lugar = lugarRepository.findById(lugarId)
                .orElseThrow(() -> new ResourceNotFoundException("Lugar no encontrado"));

        LocalDateTime fechaEvento = LocalDateTime.parse(fecha);
        validarFechaNoPasada(fechaEvento);

        Zona zonaGeneral = resolverZona(zonaGeneralId, lugar);
        Zona zonaPreferente = resolverZona(zonaPreferenteId, lugar);
        Zona zonaVip = resolverZona(zonaVipId, lugar);
        validarZonasDistintas(zonaGeneral, zonaPreferente, zonaVip);

        if (zonaGeneral != null) cantidadGeneral = zonaGeneral.getCapacidad();
        if (zonaPreferente != null) cantidadPreferente = zonaPreferente.getCapacidad();
        if (zonaVip != null) cantidadVip = zonaVip.getCapacidad();

        int totalBoletos = cantidadGeneral + cantidadPreferente + cantidadVip;
        if (totalBoletos > lugar.getCapacidad()) {
            throw new IllegalArgumentException("La suma de boletos (" + totalBoletos +
                    ") supera la capacidad del lugar (" + lugar.getCapacidad() + ")");
        }

        List<Boleto> boletosActuales = boletoRepository.findByEventoId(id);
        validarNoBajarDeVendidos(boletosActuales, "General", cantidadGeneral);
        validarNoBajarDeVendidos(boletosActuales, "Preferente", cantidadPreferente);
        validarNoBajarDeVendidos(boletosActuales, "VIP", cantidadVip);

        evento.setNombre(nombre);
        evento.setDescripcion(descripcion);
        evento.setFecha(fechaEvento);
        evento.setCapacidadTotal(capacidadTotal);
        evento.setCategoria(categoria);
        evento.setLugar(lugar);

        if (imagen != null && !imagen.isEmpty()) {
            evento.setImagenUrl(subirImagenCloudinary(imagen));
        }

        Evento eventoActualizado = eventoRepository.save(evento);

        actualizarOCrearBoleto("General", precioGeneral, cantidadGeneral, zonaGeneral, eventoActualizado);
        actualizarOCrearBoleto("Preferente", precioPreferente, cantidadPreferente, zonaPreferente, eventoActualizado);
        actualizarOCrearBoleto("VIP", precioVip, cantidadVip, zonaVip, eventoActualizado);

        return eventoActualizado;
    }

    private void actualizarOCrearBoleto(String tipo, BigDecimal precio, int cantidad, Zona zona, Evento evento) {
        Boleto boletoExistente = boletoRepository.findByEventoId(evento.getId()).stream()
                .filter(b -> b.getTipo().equals(tipo))
                .findFirst()
                .orElse(null);

        if (cantidad <= 0) {
            if (boletoExistente != null) {
                boletoRepository.delete(boletoExistente);
            }
            return;
        }

        if (boletoExistente != null) {
            int vendidos = boletoExistente.getCapacidadMax() - boletoExistente.getDisponibles();

            // Cambiar la zona invalidaría los asientos ya asignados
            Long zonaActualId = boletoExistente.getZona() != null ? boletoExistente.getZona().getId() : null;
            Long zonaNuevaId = zona != null ? zona.getId() : null;
            if (vendidos > 0 && !Objects.equals(zonaActualId, zonaNuevaId)) {
                throw new IllegalArgumentException("No puedes cambiar la zona de '" + tipo +
                        "' porque ya hay " + vendidos + " boleto(s) vendido(s).");
            }

            int nuevosDisponibles = cantidad - vendidos;

            boletoExistente.setPrecio(precio);
            boletoExistente.setCapacidadMax(cantidad);
            boletoExistente.setDisponibles(nuevosDisponibles);
            boletoExistente.setZona(zona);
            boletoRepository.save(boletoExistente);
        } else {
            Boleto nuevo = new Boleto();
            nuevo.setTipo(tipo);
            nuevo.setPrecio(precio);
            nuevo.setCapacidadMax(cantidad);
            nuevo.setDisponibles(cantidad);
            nuevo.setZona(zona);
            nuevo.setEvento(evento);
            boletoRepository.save(nuevo);
        }
    }

    /**
     * Mapa de asientos del evento
     */
    public List<ZonaAsientosDTO> obtenerMapaAsientos(Long eventoId) {
        obtenerPorId(eventoId);

        List<ZonaAsientosDTO> mapa = new ArrayList<>();

        for (Boleto boleto : boletoRepository.findByEventoId(eventoId)) {
            Zona zona = boleto.getZona();
            if (zona == null) continue;

            Set<Long> ocupados = new HashSet<>(
                    boletoClienteRepository.findAsientoIdsOcupadosPorBoleto(boleto.getId()));

            List<AsientoDTO> asientos = asientoRepository
                    .findByZonaIdOrderByFilaAscNumeroAsc(zona.getId()).stream()
                    .map(a -> new AsientoDTO(a.getId(), a.getFila(), a.getNumero(), ocupados.contains(a.getId())))
                    .toList();

            mapa.add(new ZonaAsientosDTO(boleto.getId(), boleto.getTipo(), boleto.getPrecio(),
                    zona.getId(), zona.getNombre(), zona.getFilas(), zona.getAsientosPorFila(), asientos));
        }

        return mapa;
    }

    public void eliminarEvento(Long id) {
        eventoRepository.deleteById(id);
    }

    private void validarFechaNoPasada(LocalDateTime fecha) {
        if (fecha.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha del evento no puede ser anterior a hoy.");
        }
    }

    private void validarNoBajarDeVendidos(List<Boleto> boletosActuales, String tipo, int nuevaCantidad) {
        Boleto boleto = boletosActuales.stream()
                .filter(b -> b.getTipo().equals(tipo))
                .findFirst()
                .orElse(null);

        if (boleto == null) return;

        int vendidos = boleto.getCapacidadMax() - boleto.getDisponibles();

        if (nuevaCantidad < vendidos) {
            throw new IllegalArgumentException("No puedes bajar '" + tipo + "' a " + nuevaCantidad +
                    ", ya se vendieron " + vendidos + ". El mínimo permitido es " + vendidos + ".");
        }
    }

    private String subirImagenCloudinary(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Evento crearEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Evento actualizarEventoCompleto(Long id, Evento datosNuevos) {
        Evento evento = obtenerPorId(id);
        datosNuevos.setId(evento.getId());
        return eventoRepository.save(datosNuevos);
    }

    public Evento actualizarEventoParcial(Long id, Evento datosParciales) {
        Evento evento = obtenerPorId(id);

        if (datosParciales.getNombre() != null) evento.setNombre(datosParciales.getNombre());
        if (datosParciales.getDescripcion() != null) evento.setDescripcion(datosParciales.getDescripcion());
        if (datosParciales.getFecha() != null) evento.setFecha(datosParciales.getFecha());
        if (datosParciales.getImagenUrl() != null) evento.setImagenUrl(datosParciales.getImagenUrl());
        if (datosParciales.getEstado() != null) evento.setEstado(datosParciales.getEstado());

        return eventoRepository.save(evento);
    }

    //Alterna el estado del evento (ACTIVO/INACTIVO)
    @Transactional
    public void alternarEstadoInactivo(Long id) {
        // Busca el evento
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el evento con id " + id));

        // Se invirte el estado. Si era false se vuelve true, si era true se vuelve false
        evento.setInactivo(!evento.isInactivo());

        eventoRepository.save(evento);
    }

    // Contar todos los eventos inactivos
    public long contarInactivos() {
        return eventoRepository.countByInactivoTrue();
    }

    //Contar todos los eventos activos
    public long contarActivos() {
        return eventoRepository.countByInactivoFalse();
    }


}