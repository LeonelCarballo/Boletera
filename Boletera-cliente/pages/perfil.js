import AuthService from "../services/AuthService.js";
import PerfilService from "../services/PerfilService.js";

document.addEventListener('DOMContentLoaded', async () => {
    renderHeader();

    if (!AuthService.isAutenticate()) {
        window.location.href = `login.html?redirect=${encodeURIComponent('perfil.html')}`;
        return;
    }

    try {
        const perfil = await PerfilService.obtener();
        llenarFormulario(perfil);
    } catch (error) {
        // Token expirado o inválido: cerrar sesión y mandar a login
        AuthService.logout();
        window.location.href = `login.html?redirect=${encodeURIComponent('perfil.html')}`;
        return;
    }

    document.getElementById('perfil-form').addEventListener('submit', guardarPerfil);
    document.getElementById('password-form').addEventListener('submit', cambiarContrasenia);
});

function renderHeader() {
    const headerActions = document.getElementById('header-actions');
    const mainNav = document.getElementById('main-nav');

    if (AuthService.isAutenticate()) {
        const user = AuthService.getUser();

        headerActions.innerHTML = `
            <a href="perfil.html" class="link-entrar" id="header-saludo"></a>
            <button type="button" class="btn-primary" id="logout-btn">Cerrar sesión</button>
        `;
        document.getElementById('header-saludo').textContent = `Hola, ${user?.nombre || 'Usuario'}`;
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
    }
}

function llenarFormulario(perfil) {
    document.getElementById('perfil-email').value = perfil.email || '';
    document.getElementById('perfil-nombre').value = perfil.nombre || '';
    document.getElementById('perfil-telefono').value = perfil.telefono || '';
}

async function guardarPerfil(e) {
    e.preventDefault();

    const nombre = document.getElementById('perfil-nombre').value;
    const telefono = document.getElementById('perfil-telefono').value;

    try {
        const perfil = await PerfilService.actualizar(nombre, telefono);
        llenarFormulario(perfil);

        // Actualizar el nombre guardado
        const user = AuthService.getUser();
        if (user) {
            user.nombre = perfil.nombre;
            localStorage.setItem('user', JSON.stringify(user));
        }
        renderHeader();

        mostrarAlerta('perfil-alert', 'Perfil actualizado correctamente', true);
    } catch (error) {
        mostrarAlerta('perfil-alert', error.message, false);
    }
}

async function cambiarContrasenia(e) {
    e.preventDefault();

    const actual = document.getElementById('pw-actual').value;
    const nueva = document.getElementById('pw-nueva').value;
    const confirmar = document.getElementById('pw-confirmar').value;

    if (nueva !== confirmar) {
        mostrarAlerta('password-alert', 'Las contraseñas nuevas no coinciden', false);
        return;
    }

    try {
        const respuesta = await PerfilService.cambiarContrasenia(actual, nueva);
        document.getElementById('password-form').reset();
        mostrarAlerta('password-alert', respuesta.mensaje || 'Contraseña actualizada', true);
    } catch (error) {
        mostrarAlerta('password-alert', error.message, false);
    }
}

function mostrarAlerta(id, mensaje, esExito) {
    const alerta = document.getElementById(id);
    alerta.textContent = mensaje;
    alerta.className = 'perfil-alert ' + (esExito ? 'perfil-alert-ok' : 'perfil-alert-error');
    alerta.style.display = 'block';
}
