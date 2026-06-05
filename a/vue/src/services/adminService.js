import { createAuthApi } from './authService'

const api = createAuthApi()

export async function fetchAdminMensajes() {
  const response = await api.get('/api/admin/contacto/mensajes')
  return Array.isArray(response.data) ? response.data : []
}

export async function fetchAdminColaboraciones() {
  const response = await api.get('/api/admin/contacto/colaboraciones')
  return Array.isArray(response.data) ? response.data : []
}

export async function fetchAdminSolicitudesComercios() {
  const response = await api.get('/api/admin/comercios-pendientes')
  return Array.isArray(response.data) ? response.data : []
}

export async function aprobarComercio(comercioId) {
  const response = await api.post('/api/admin/comercios/aprobar', {
    comercioId
  })
  return response.data
}

export async function rechazarComercio(comercioId, motivoRechazo) {
  const response = await api.post('/api/admin/comercios/rechazar', {
    comercioId,
    motivoRechazo
  })
  return response.data
}

export async function eliminarComercio(comercioId) {
  const response = await api.delete(`/api/admin/comercios/${comercioId}`)
  return response.data
}

export async function aprobarColaboracion(solicitudId) {
  const response = await api.post('/api/admin/contacto/colaboraciones/aprobar', {
    solicitudId,
  })
  return response.data
}

export async function rechazarColaboracion(solicitudId, motivoRechazo) {
  const response = await api.post('/api/admin/contacto/colaboraciones/rechazar', {
    solicitudId,
    motivoRechazo,
  })
  return response.data
}

export async function fetchAdminComerciosActivos() {
  const response = await api.get('/api/comercios') 
  return Array.isArray(response.data) ? response.data : []
}