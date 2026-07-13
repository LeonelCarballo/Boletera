import AuthService from "../services/AuthService.js";
import EventoService from "../services/EventoService.js";

document.addEventListener('DOMContentLoaded', () => {
    renderHeader();
    cargarEventos();
    configurarBusqueda();
});

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

async function cargarEventos() {
    const container = document.getElementById('eventos-container');

    //si la cuenta es activa muestra los eventos, inactiva los oculta
    const usuarioActual = AuthService.getUser();
    if (AuthService.isAutenticate() && usuarioActual && 
        (usuarioActual.activo === false || usuarioActual.activo === 0 || usuarioActual.activo === "false")) {
        
        container.innerHTML = '<p class="subtitle">Tu cuenta se encuentra suspendida temporalmente. No hay eventos disponibles.</p>';
        return;
    }

    try {
        
        const todosLosEventos = await EventoService.getAll();

        //filtro para que muestre unicamente los eventos ACTIVOS
        const eventos = todosLosEventos.filter(evento => !evento.inactivo);

        if (!eventos.length) {
            container.innerHTML = '<p class="subtitle">No hay eventos disponibles por el momento.</p>';
            return;
        }

        container.innerHTML = '';

        eventos.forEach(evento => {
            const card = document.createElement('article');
            card.className = 'tarjeta-evento';

            const fecha = evento.fecha ? formatearFecha(evento.fecha) : '';
            const lugarTexto = evento.lugar ? `${evento.lugar.nombre}, ${evento.lugar.ciudad}` : 'Lugar por confirmar';
            const imagen = evento.imagenUrl || 'https://placehold.co/600x400?text=Sin+imagen';
            card.innerHTML = `
                <div class="tarjeta-imagen">
                    <img src="${imagen}" alt="${evento.nombre}" onerror="this.src='https://placehold.co/600x400?text=Sin+imagen'"">
                </div>
                <div class="tarjeta-contenido">
                    <time class="tarjeta-fecha">${fecha}</time>
                    <h3 class="tarjeta-titulo">${evento.nombre}</h3>
                    <p class="tarjeta-lugar">
                        <img src="/imgs/icon-ubicacion.svg" alt="" class="icono-inline">
                        <span>${lugarTexto}</span>
                    </p>
                    <div class="tarjeta-footer">
                        <a href="evento-detalle.html?id=${evento.id}" class="btn-boletos">Boletos</a>
                    </div>
                </div>
            `;

            container.appendChild(card);
        });

    } catch (error) {
        container.innerHTML = `<p class="subtitle">No se pudieron cargar los eventos: ${error.message}</p>`;
    }
}

function formatearFecha(fechaISO) {
    const fecha = new Date(fechaISO);
    return fecha.toLocaleDateString('es-MX', { weekday: 'long', day: '2-digit', month: 'short' });
}

function configurarBusqueda() {
    const form = document.getElementById('search-form');

    form.addEventListener('submit', (e) => {
        e.preventDefault();
        const evento = document.getElementById('evento').value;
        const lugar = document.getElementById('lugar').value;

        const params = new URLSearchParams();
        if (evento) params.set('evento', evento);
        if (lugar) params.set('lugar', lugar);

        window.location.href = `eventos.html?${params.toString()}`;
    });
}