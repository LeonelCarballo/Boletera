const BASE_URL = 'http://localhost:8080/api';

class AuthService {
    static async login(email, contrasenia) {
        const response = await fetch(`${BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, contrasenia })
        });

        const data = await response.json();

        if (!response.ok) {
            const error = data.error || 'Error al iniciar sesión';
            throw new Error(error);
        }

        if (data.token) {
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify({
                nombre: data.nombre,
                email: data.email,
                rol: data.rol,
                activo: data.activo
            }));
        }

        return data;
    }

    static async register(nombre, email, contrasenia, telefono) {
        const response = await fetch(`${BASE_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nombre, email, contrasenia, telefono })
        });

        const data = await response.json();

        if (!response.ok) {
            const error = data.error || 'Error al crear la cuenta';
            throw new Error(error);
        }

        return data;
    }

    static logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    }

    static isAutenticate() {
        return !!localStorage.getItem('token');
    }

    static getToken() {
        return localStorage.getItem('token');
    }

    static getUser() {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    }
}

export default AuthService;