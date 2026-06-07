import axios from 'axios';
import { apiUrl } from '../config/api';

const API_URL = apiUrl('/api/reservas');

export const reservaService = {
  // Para que Paqui cargue su lista de citas
  obtenerReservasComercio(comercioId) {
    return axios.get(`${API_URL}/comercio/${comercioId}`).then(res => res.data);
  },
  
  // Para que el cliente reserve (lo usaremos luego)
  crearReserva(reserva) {
    return axios.post(`${API_URL}/crear`, reserva).then(res => res.data);
  }
};