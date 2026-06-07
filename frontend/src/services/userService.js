import { createAuthApi, saveAuth, getAuth } from './authService'

const api = createAuthApi()

export async function updateProfile(nombre, email) {
  const response = await api.put('/api/auth/me', { nombre, email })
  const current = getAuth()
  if (current) {
    saveAuth({ ...current, nombre: response.data.nombre, email: response.data.email })
  }
  return response.data
}

export async function changePassword(contrasenaActual, nuevaContrasena, confirmarContrasena) {
  const response = await api.post('/api/auth/change-password', {
    contrasenaActual,
    nuevaContrasena,
    confirmarContrasena,
  })
  return response.data
}

export async function deleteAccount() {
  const response = await api.delete('/api/auth/me')
  return response.data
}
