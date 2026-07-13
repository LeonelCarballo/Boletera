import AuthService from "./AuthService.js";

const BASE_URL = 'http://localhost:8080/api';

class PerfilService {

    static async obtener() {
        const response = await fetch(`${BASE_URL}/me`, {
            headers: {
                'Authorization': `Bearer ${AuthService.getToken()}`
            }
        });

        const data = await response.json();

        if (!response.ok) {
            const error = data.error || 'No se pudo cargar tu perfil';
            throw new Error(error);
        }

        return data;
    }

    static async actualizar(nombre, telefono) {
        const response = await fetch(`${BASE_URL}/me`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${AuthService.getToken()}`
            },
            body: JSON.stringify({ nombre, telefono })
        });

        const data = await response.json();

        if (!response.ok) {
            const error = data.error || 'No se pudo actualizar tu perfil';
            throw new Error(error);
        }

        return data;
    }

    static async cambiarContrasenia(contraseniaActual, contraseniaNueva) {
        const response = await fetch(`${BASE_URL}/me/password`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${AuthService.getToken()}`
            },
            body: JSON.stringify({ contraseniaActual, contraseniaNueva })
        });

        const data = await response.json();

        if (!response.ok) {
            const error = data.error || 'No se pudo cambiar la contraseña';
            throw new Error(error);
        }

        return data;
    }
}

export default PerfilService;
