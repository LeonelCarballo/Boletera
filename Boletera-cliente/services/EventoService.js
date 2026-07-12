const BASE_URL = 'http://localhost:8080/api';

class EventoService {

    static async getAll() {
        const response = await fetch(`${BASE_URL}/eventos`);
        const data = await response.json();

        if (!response.ok) {
            const error = data.error || 'Error al obtener los eventos';
            throw new Error(error);
        }

        return data;
    }

    static async getById(id) {
        const response = await fetch(`${BASE_URL}/eventos/${id}`);
        const data = await response.json();

        if (!response.ok) {
            const error = data.error || 'Evento no encontrado';
            throw new Error(error);
        }

        return data;
    }
}

export default EventoService;