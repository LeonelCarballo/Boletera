import AuthService from "../services/AuthService.js";
import CompraService from "../services/CompraService.js";
import QRCode from "https://esm.sh/qrcode@1.5.3";

document.addEventListener('DOMContentLoaded', async () => {
    renderHeader();

    if (!AuthService.isAutenticate()) {
        window.location.href = `login.html?redirect=${encodeURIComponent('mis-tickets.html')}`;
        return;
    }

    try {
        const tickets = await CompraService.misTickets();
        renderTickets(tickets);
    } catch (error) {
        document.getElementById('tickets-container').innerHTML =
            `<p class="subtitle">No se pudieron cargar tus tickets: ${error.message}</p>`;
    }
});

function renderHeader() {
    const headerActions = document.getElementById('header-actions');
    const mainNav = document.getElementById('main-nav');

    if (AuthService.isAutenticate()) {
        const user = AuthService.getUser();

        headerActions.innerHTML = `
            <span class="link-entrar">Hola, ${user?.nombre || 'Usuario'}</span>
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

function renderTickets(tickets) {
    const container = document.getElementById('tickets-container');

    if (!tickets.length) {
        container.innerHTML = `
            <div class="tickets-vacio">
                <p class="subtitle">Aún no tienes boletos comprados.</p>
                <a href="eventos.html" class="btn-primary">Explorar eventos</a>
            </div>
        `;
        return;
    }

    // Los tickets más recientes primero
    const ordenados = [...tickets].sort((a, b) => new Date(b.fechaCompra) - new Date(a.fechaCompra));

    container.innerHTML = '';

    ordenados.forEach(ticket => {
        const canvasId = `qr-${ticket.boletoClienteId}`;
        const estadoClase = (ticket.estado || '').toLowerCase();

        const card = document.createElement('article');
        card.className = 'ticket-card';

        card.innerHTML = `
            <div class="ticket-card-header">
                <span class="tarjeta-categoria">${ticket.tipoBoleto}</span>
                <span class="ticket-estado ticket-estado-${estadoClase}">${ticket.estado}</span>
            </div>
            <h3 class="ticket-evento-nombre">${ticket.eventoNombre || 'Evento'}</h3>
            <p class="ticket-meta">
                <img src="/imgs/icon-ubicacion.svg" alt="" class="icono-inline">
                ${ticket.eventoFecha ? formatearFecha(ticket.eventoFecha) : ''}${ticket.lugarNombre ? ` · ${ticket.lugarNombre}, ${ticket.lugarCiudad}` : ''}
            </p>
            <div class="ticket-qr-wrap">
                <canvas id="${canvasId}"></canvas>
            </div>
            <p class="ticket-codigo">${ticket.codigoQr}</p>
            <p class="ticket-precio">$${formatearPrecio(ticket.precio)} MXN</p>
        `;

        container.appendChild(card);

        QRCode.toCanvas(document.getElementById(canvasId), ticket.codigoQr, {
            width: 160,
            margin: 1,
            color: { dark: '#1A1A1A', light: '#FFFFFF' }
        }).catch(err => console.error('No se pudo generar el QR:', err));
    });
}

function formatearPrecio(valor) {
    return Number(valor).toLocaleString('es-MX', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function formatearFecha(fechaISO) {
    const fecha = new Date(fechaISO);
    return fecha.toLocaleDateString('es-MX', { day: '2-digit', month: 'short', year: 'numeric' });
}