import AuthService from "./AuthService.js";

const BASE_URL = 'http://localhost:8080/api';

class CompraService {

    static async crear(items) {
        const response = await fetch(`${BASE_URL}/compras`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${AuthService.getToken()}`
            },
            body: JSON.stringify({ boletos: items })
        });

        const data = await response.json();

        if (!response.ok) {
            const error = data.error || 'No se pudo completar la compra';
            throw new Error(error);
        }

        return data;
    }

    static async misTickets() {
        const response = await fetch(`${BASE_URL}/compras/mis-tickets`, {
            headers: {
                'Authorization': `Bearer ${AuthService.getToken()}`
            }
        });

        const data = await response.json();

        if (!response.ok) {
            const error = data.error || 'No se pudieron obtener tus tickets';
            throw new Error(error);
        }

        return data;
    }
}

export default CompraService;