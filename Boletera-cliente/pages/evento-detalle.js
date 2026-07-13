import AuthService from "../services/AuthService.js";
import EventoService from "../services/EventoService.js";
import CompraService from "../services/CompraService.js";

let cantidades = {};
let boletosActuales = [];

document.addEventListener('DOMContentLoaded', async () => {
    renderHeader();

    const id = obtenerIdDesdeURL();
    if (!id) {
        mostrarError('No se especificó ningún evento.');
        return;
    }

    try {
        const evento = await EventoService.getById(id);
        renderEvento(evento);
    } catch (error) {
        mostrarError(error.message);
    }

    document.getElementById('btn-reservar').addEventListener('click', reservarBoletos);
});

function obtenerIdDesdeURL() {
    const params = new URLSearchParams(window.location.search);
    return params.get('id');
}

function renderHeader() {
    const headerActions = document.getElementById('header-actions');
    const mainNav = document.getElementById('main-nav');

    if (AuthService.isAutenticate()) {
        const user = AuthService.getUser();

        headerActions.innerHTML = `
            <a href="perfil.html" class="link-entrar">Hola, ${user?.nombre || 'Usuario'}</a>
            <button type="button" class="btn-primary" id="logout-btn">Cerrar sesión</button>
        `;
        document.getElementById('logout-btn').addEventListener('click', () => {
            AuthService.logout();
            window.location.href = 'index.html';
        });

        if (!document.getElementById('nav-mis-tickets')) {
            mainNav.insertAdjacentHTML('beforeend',
                '<a href="mis-tickets.html" id="nav-mis-tickets" class="nav-link">Mis Tickets</a>'
            );
        }

    } else {
        headerActions.innerHTML = `
            <a href="login.html" class="link-entrar">Entrar</a>
            <a href="crear-cuenta.html" class="btn-primary">Regístrate</a>
        `;

        const existente = document.getElementById('nav-mis-tickets');
        if (existente) existente.remove();
    }
}

function mostrarError(mensaje) {
    document.getElementById('detalle-loading').style.display = 'none';
    document.getElementById('detalle-contenido').style.display = 'none';
    const errorSection = document.getElementById('detalle-error');
    document.getElementById('detalle-error-msg').textContent = mensaje;
    errorSection.style.display = 'block';
}

function renderEvento(evento) {
    document.getElementById('detalle-loading').style.display = 'none';
    document.getElementById('detalle-contenido').style.display = 'grid';

    document.title = `TicketPlace - ${evento.nombre}`;

    const imagen = evento.imagenUrl || 'https://placehold.co/1200x600?text=Sin+imagen';
    const imgEl = document.getElementById('detalle-imagen');
    imgEl.src = imagen;
    imgEl.alt = evento.nombre;
    imgEl.onerror = () => { imgEl.src = 'https://placehold.co/1200x600?text=Sin+imagen'; };

    document.getElementById('detalle-badge').textContent = evento.categoria || '';

    document.getElementById('detalle-nombre').textContent = evento.nombre || '';

    document.getElementById('detalle-fecha').textContent = evento.fecha ? formatearFecha(evento.fecha) : '';
    document.getElementById('detalle-hora').textContent = formatearRangoHora(evento.fecha, evento.fechaFin);

    const lugarCorto = evento.lugar ? `${evento.lugar.nombre}, ${evento.lugar.ciudad}` : 'Lugar por confirmar';
    document.getElementById('detalle-lugar-corto').textContent = lugarCorto;

    // La descripción puede traer saltos de línea; los respetamos como párrafos
    const descripcionEl = document.getElementById('detalle-descripcion');
    descripcionEl.innerHTML = '';
    (evento.descripcion || 'Sin descripción disponible.')
        .split(/\n+/)
        .filter(p => p.trim())
        .forEach(parrafo => {
            const p = document.createElement('p');
            p.textContent = parrafo;
            descripcionEl.appendChild(p);
        });

    renderVenue(evento.lugar);
    renderBoletos(evento.boletos || []);
}

function renderVenue(lugar) {
    const card = document.getElementById('detalle-venue-card');

    if (!lugar) {
        card.style.display = 'none';
        return;
    }

    document.getElementById('detalle-venue-nombre').textContent = lugar.nombre || '';
    document.getElementById('detalle-venue-direccion').textContent =
        `${lugar.direccion || ''}, ${lugar.ciudad || ''}, ${lugar.estado || ''}`;
}

function renderBoletos(boletos) {
    boletosActuales = boletos;
    cantidades = {};
    boletos.forEach(b => { cantidades[b.id] = 0; });

    const container = document.getElementById('boletos-container');

    if (!boletos.length) {
        container.innerHTML = '<p class="subtitle">Aún no hay boletos disponibles para este evento.</p>';
        actualizarTotal();
        return;
    }

    container.innerHTML = '';

    boletos.forEach(boleto => {
        const agotado = (boleto.disponibles ?? 0) <= 0;
        const esVip = (boleto.tipo || '').toLowerCase().includes('vip');

        const card = document.createElement('div');
        card.className = 'boleto-selector' + (agotado ? ' boleto-agotado' : '');
        card.dataset.boletoId = boleto.id;

        card.innerHTML = `
            ${esVip ? '<span class="boleto-vip-badge">VIP</span>' : ''}
            <div class="boleto-selector-top">
                <div class="boleto-selector-info">
                    <span class="boleto-tipo">${boleto.tipo}</span>
                    <span class="boleto-disponibilidad">${agotado ? 'Agotado' : `${boleto.disponibles} disponibles`}</span>
                </div>
                <span class="boleto-precio">$${formatearPrecio(boleto.precio)}</span>
            </div>
            <div class="boleto-selector-bottom">
                <div class="boleto-stepper" ${agotado ? 'style="visibility:hidden;"' : ''}>
                    <button type="button" class="stepper-btn stepper-menos" data-accion="menos" aria-label="Quitar boleto">−</button>
                    <span class="stepper-cantidad">0</span>
                    <button type="button" class="stepper-btn stepper-mas" data-accion="mas" aria-label="Agregar boleto">+</button>
                </div>
                <span class="boleto-por-boleto">Por boleto</span>
            </div>
        `;

        container.appendChild(card);

        if (!agotado) {
            const menosBtn = card.querySelector('.stepper-menos');
            const masBtn = card.querySelector('.stepper-mas');
            menosBtn.addEventListener('click', () => cambiarCantidad(boleto, -1, card));
            masBtn.addEventListener('click', () => cambiarCantidad(boleto, 1, card));
        }
    });

    actualizarTotal();
}

function cambiarCantidad(boleto, delta, card) {
    const actual = cantidades[boleto.id] || 0;
    const nueva = Math.min(Math.max(actual + delta, 0), boleto.disponibles);

    if (nueva === actual) return;

    cantidades[boleto.id] = nueva;
    card.querySelector('.stepper-cantidad').textContent = nueva;
    actualizarTotal();
}

function actualizarTotal() {
    let total = 0;
    let totalBoletos = 0;

    boletosActuales.forEach(b => {
        const cant = cantidades[b.id] || 0;
        total += cant * Number(b.precio);
        totalBoletos += cant;
    });

    document.getElementById('boletos-total').textContent = `$${formatearPrecio(total)}`;

    const btn = document.getElementById('btn-reservar');
    const nota = document.getElementById('boletos-nota');

    btn.disabled = totalBoletos === 0;
    nota.textContent = totalBoletos === 0
        ? 'Selecciona al menos un boleto'
        : `${totalBoletos} boleto${totalBoletos > 1 ? 's' : ''} seleccionado${totalBoletos > 1 ? 's' : ''}`;
}

async function reservarBoletos() {
    const nota = document.getElementById('boletos-nota');

    if (!AuthService.isAutenticate()) {
        const id = obtenerIdDesdeURL();
        window.location.href = `login.html?redirect=${encodeURIComponent(`evento-detalle.html?id=${id}`)}`;
        return;
    }

    const items = boletosActuales
        .filter(b => (cantidades[b.id] || 0) > 0)
        .map(b => ({ boletoId: b.id, cantidad: cantidades[b.id] }));

    if (!items.length) return;

    const btn = document.getElementById('btn-reservar');
    btn.disabled = true;
    nota.textContent = 'Procesando tu compra...';

    try {
        await CompraService.crear(items);
        nota.textContent = '¡Compra exitosa! Redirigiendo a tus tickets...';
        setTimeout(() => {
            window.location.href = 'mis-tickets.html';
        }, 1200);
    } catch (error) {
        nota.textContent = error.message;
        btn.disabled = false;
    }
}

function formatearPrecio(valor) {
    return Number(valor).toLocaleString('es-MX', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function formatearFecha(fechaISO) {
    const fecha = new Date(fechaISO);
    return fecha.toLocaleDateString('es-MX', { day: '2-digit', month: 'short', year: 'numeric' });
}

function formatearRangoHora(inicioISO, finISO) {
    if (!inicioISO) return '';
    const inicio = formatearHora(inicioISO);
    if (!finISO) return inicio;
    return `${inicio} - ${formatearHora(finISO)}`;
}

function formatearHora(fechaISO) {
    const fecha = new Date(fechaISO);
    return fecha.toLocaleTimeString('es-MX', { hour: '2-digit', minute: '2-digit', hour12: false });
}