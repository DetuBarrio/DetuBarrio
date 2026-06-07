import axios from 'axios'
import { apiUrl } from '../config/api'

export async function fetchCategorias() {
  const respuesta = await axios.get(apiUrl('/api/categorias'))
  return Array.isArray(respuesta.data) ? respuesta.data : []
}

export async function fetchComercios(params = {}) {
  const respuesta = await axios.get(apiUrl('/api/comercios'), { params })
  return respuesta.data || { content: [], totalPages: 0, totalElements: 0, number: 0 }
}

export async function fetchComercioById(comercioId) {
  const respuesta = await axios.get(apiUrl(`/api/comercios/${comercioId}`))
  return respuesta.data || null
}