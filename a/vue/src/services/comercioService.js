import axios from 'axios'
import { apiUrl } from '../config/api'

export async function fetchCategorias() {
  const respuesta = await axios.get(apiUrl('/api/categorias'))
  return Array.isArray(respuesta.data) ? respuesta.data : []
}

export async function fetchComercios() {
  const respuesta = await axios.get(apiUrl('/api/comercios'))
  return Array.isArray(respuesta.data) ? respuesta.data : []
}

export async function fetchComercioById(comercioId) {
  const respuesta = await axios.get(apiUrl(`/api/comercios/${comercioId}`))
  return respuesta.data || null
}