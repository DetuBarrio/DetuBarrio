import axios from 'axios'
import { apiUrl } from '../config/api'

export async function enviarMensajeContacto(payload) {
  const response = await axios.post(apiUrl('/api/contacto/mensaje'), payload)
  return response.data
}

export async function solicitarColaboracion(payload) {
  const response = await axios.post(apiUrl('/api/contacto/colaboracion'), payload)
  return response.data
}