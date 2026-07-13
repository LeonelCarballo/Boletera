import AuthService from "../services/AuthService.js";

document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    const errorBox = document.getElementById('error-message');

    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            errorBox.style.display = 'none';

            const email = document.getElementById('email').value;
            const contrasenia = document.getElementById('contrasenia').value;

            try {
                await AuthService.login(email, contrasenia);
                const redirect = new URLSearchParams(window.location.search).get('redirect');
                window.location.href = redirect || 'index.html';
            } catch (error) {
                errorBox.textContent = error.message;
                errorBox.style.display = 'block';
            }
        });
    }
});