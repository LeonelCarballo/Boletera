import AuthService from "../services/AuthService.js";

document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('register-form');
    const errorBox = document.getElementById('error-message');

    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            errorBox.style.display = 'none';

            const nombre = document.getElementById('nombre').value;
            const email = document.getElementById('email').value;
            const contrasenia = document.getElementById('contrasenia').value;
            const confirmar = document.getElementById('confirmar_contrasenia').value;
            const telefono = document.getElementById('telefono').value;

            if (contrasenia !== confirmar) {
                errorBox.textContent = 'Las contraseñas no coinciden';
                errorBox.style.display = 'block';
                return;
            }

            try {
                await AuthService.register(nombre, email, contrasenia, telefono);
                window.location.href = 'login.html';
            } catch (error) {
                errorBox.textContent = error.message;
                errorBox.style.display = 'block';
            }
        });
    }
});