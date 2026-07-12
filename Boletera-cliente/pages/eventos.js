import AuthService from "../services/AuthService.js";
import EventoService from "../services/EventoService.js";

const EVENTOS_POR_PAGINA = 9;

let todosLosEventos = [];
let paginaActual = 1;

document.addEventListener('DOMContentLoaded', async () => {
    renderHeader();
    precargarFiltrosDesdeURL();

    try {
        todosLosEventos = await EventoService.getAll();
        aplicarFiltros();
    } catch (error) {
        document.getElementById('eventos-container').innerHTML =
            `<p class="subtitle">No se pudieron cargar los eventos: ${error.message}</p>`;
    }

    configurarEventosDeFiltros();
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


function precargarFiltrosDesdeURL() {
    const params = new URLSearchParams(window.location.search);
    const evento = params.get('evento');
    const lugar = params.get('lugar');
    const categoria = params.get('categoria');

    if (lugar) {
        document.getElementById('ubicacion').value = lugar;
    }
    if (evento) {
        // Reusamos el mismo input de ubicación como búsqueda general de texto
        document.getElementById('ubicacion').dataset.textoLibre = evento;
    }

    if (categoria) {
        const checkbox = document.querySelector(`input[name="categoria"][value="${categoria}"]`);
        if (checkbox) {
            document.getElementById('cat-todos').checked = false;
            checkbox.checked = true;
        }
    }
}

function configurarEventosDeFiltros() {
    document.getElementById('ubicacion').addEventListener('input', () => {
        paginaActual = 1;
        aplicarFiltros();
    });

    document.querySelectorAll('input[name="categoria"]').forEach(checkbox => {
        checkbox.addEventListener('change', (e) => {
            const todos = document.getElementById('cat-todos');

            if (e.target.id === 'cat-todos') {
                if (e.target.checked) {
                    document.querySelectorAll('input[name="categoria"]:not(#cat-todos)')
                        .forEach(c => c.checked = false);
                }
            } else if (e.target.checked) {
                todos.checked = false;
            }

            paginaActual = 1;
            aplicarFiltros();
        });
    });
}

function aplicarFiltros() {
    const ubicacionInput = document.getElementById('ubicacion');
    const textoUbicacion = ubicacionInput.value.trim().toLowerCase();
    const textoLibre = (ubicacionInput.dataset.textoLibre || '').trim().toLowerCase();

    const categoriasSeleccionadas = Array.from(
        document.querySelectorAll('input[name="categoria"]:checked:not(#cat-todos)')
    ).map(c => c.value);

    let filtrados = todosLosEventos.filter(evento => {
        const coincideUbicacion = !textoUbicacion ||
            (evento.lugar?.ciudad?.toLowerCase().includes(textoUbicacion)) ||
            (evento.lugar?.nombre?.toLowerCase().includes(textoUbicacion));

        const coincideTextoLibre = !textoLibre ||
            evento.nombre?.toLowerCase().includes(textoLibre);

        const coincideCategoria = categoriasSeleccionadas.length === 0 ||
            categoriasSeleccionadas.includes(evento.categoria);

        return coincideUbicacion && coincideTextoLibre && coincideCategoria;
    });

    renderEventos(filtrados);
    renderPaginacion(filtrados.length);
}

function renderEventos(eventos) {
    const container = document.getElementById('eventos-container');

    if (!eventos.length) {
        container.innerHTML = '<p class="subtitle">No hay eventos que coincidan con tu búsqueda.</p>';
        return;
    }

    const inicio = (paginaActual - 1) * EVENTOS_POR_PAGINA;
    const pagina = eventos.slice(inicio, inicio + EVENTOS_POR_PAGINA);

    container.innerHTML = '';

    pagina.forEach(evento => {
        const card = document.createElement('article');
        card.className = 'tarjeta-evento';

        const fecha = evento.fecha ? formatearFecha(evento.fecha) : '';
        const lugarTexto = evento.lugar ? `${evento.lugar.nombre}, ${evento.lugar.ciudad}` : 'Lugar por confirmar';
        const imagen = evento.imagenUrl || 'https://placehold.co/600x400?text=Sin+imagen';

        card.innerHTML = `
            <div class="tarjeta-imagen">
                <img src="${imagen}" alt="${evento.nombre}" onerror="this.src='https://placehold.co/600x400?text=Sin+imagen'">
            </div>
            <div class="tarjeta-contenido">
                <div class="tarjeta-meta">
                    <span class="tarjeta-categoria">${evento.categoria}</span>
                    <time>${fecha}</time>
                </div>
                <h3 class="tarjeta-titulo">${evento.nombre}</h3>
                <p class="tarjeta-lugar">
                    <img src="/imgs/icon-ubicacion.svg" alt="" class="icono-inline">
                    <span>${lugarTexto}</span>
                </p>
                <div class="tarjeta-footer catalogo-footer">
                    <a href="evento-detalle.html?id=${evento.id}" class="btn-flecha" aria-label="Ver evento">→</a>
                </div>
            </div>
        `;

        container.appendChild(card);
    });
}

function renderPaginacion(totalEventos) {
    const nav = document.getElementById('paginacion');
    const totalPaginas = Math.ceil(totalEventos / EVENTOS_POR_PAGINA);

    if (totalPaginas <= 1) {
        nav.innerHTML = '';
        return;
    }

    let html = '';

    html += `<a href="#" class="pagina-btn" data-pagina="${paginaActual - 1}" aria-label="Página anterior" ${paginaActual === 1 ? 'style="pointer-events:none;opacity:0.4;"' : ''}>‹</a>`;

    for (let i = 1; i <= totalPaginas; i++) {
        html += `<a href="#" class="pagina-numero ${i === paginaActual ? 'activo' : ''}" data-pagina="${i}">${i}</a>`;
    }

    html += `<a href="#" class="pagina-btn" data-pagina="${paginaActual + 1}" aria-label="Página siguiente" ${paginaActual === totalPaginas ? 'style="pointer-events:none;opacity:0.4;"' : ''}>›</a>`;

    nav.innerHTML = html;

    nav.querySelectorAll('[data-pagina]').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            paginaActual = parseInt(link.dataset.pagina);
            aplicarFiltros();
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });
    });
}

function formatearFecha(fechaISO) {
    const fecha = new Date(fechaISO);
    return fecha.toLocaleDateString('es-MX', { day: '2-digit', month: 'short', year: 'numeric' });
}