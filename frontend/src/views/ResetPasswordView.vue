<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { apiUrl } from '../config/api'

const route = useRoute()

const token = ref('')
const password = ref('')
const confirmPassword = ref('')

const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

onMounted(() => {
  // Extraemos el token que viene en la URL (?token=xxxx)
  const tokenParam = route.query.token
  if (!tokenParam) {
    errorMessage.value = 'El enlace de recuperación no es válido o ha expirado.'
  } else {
    token.value = tokenParam
  }
})

async function handleResetPassword() {
  // Validación rápida en cliente antes de enviar
  if (password.value !== confirmPassword.value) {
    errorMessage.value = 'Las contraseñas no coinciden.'
    return
  }

  if (password.value.length < 6) {
    errorMessage.value = 'La contraseña debe tener al menos 6 caracteres.'
    return
  }

  loading.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    const response = await fetch(apiUrl('/api/auth/reset-password'), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        token: token.value,
        nuevaContrasena: password.value // 🔧 CORREGIDO: Adaptado al nombre de tu DTO en Spring
      }),
    })

    const data = await response.json().catch(() => null)

    if (!response.ok) {
      throw new Error(data?.error || 'No se pudo restablecer la contraseña')
    }

    successMessage.value = '¡Contraseña actualizada con éxito! Ya puedes iniciar sesión.'
    password.value = ''
    confirmPassword.value = ''
  } catch (error) {
    console.error('Error al cambiar la contraseña:', error)
    errorMessage.value = error.message || 'Ocurrió un error. El enlace podría haber caducado.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="login-shell">
    <section class="login-form-panel w-100">
      <div class="login-card shadow-lg mx-auto">
        
        <div class="d-flex align-items-center justify-content-between mb-4">
          <div>
            <p class="text-uppercase text-muted small mb-1">Nueva credencial</p>
            <h2 class="h3 fw-bold mb-0">Restablecer contraseña</h2>
          </div>
          <RouterLink class="btn btn-outline-secondary btn-sm" to="/login">Cancelar</RouterLink>
        </div>

        <div v-if="errorMessage" class="alert alert-danger border-0 mb-3">{{ errorMessage }}</div>
        <div v-if="successMessage" class="alert alert-success border-0 mb-3">{{ successMessage }}</div>

        <form v-if="token && !successMessage" @submit.prevent="handleResetPassword">
          <div class="mb-3">
            <label class="form-label">Nueva contraseña</label>
            <input 
              v-model="password" 
              type="password" 
              class="form-control form-control-lg" 
              placeholder="Mínimo 6 caracteres" 
              required
              :disabled="loading"
            >
          </div>

          <div class="mb-4">
            <label class="form-label">Confirmar nueva contraseña</label>
            <input 
              v-model="confirmPassword" 
              type="password" 
              class="form-control form-control-lg" 
              placeholder="Repite tu contraseña" 
              required
              :disabled="loading"
            >
          </div>

          <button class="btn btn-primary w-100 btn-lg fw-bold" type="submit" :disabled="loading">
            {{ loading ? 'Actualizando...' : 'Guardar nueva contraseña' }}
          </button>
        </form>

        <div v-else-if="successMessage" class="text-center mt-2">
          <RouterLink to="/login" class="btn btn-primary btn-lg w-100 fw-bold">
            Iniciar sesión ahora
          </RouterLink>
        </div>

      </div>
    </section>
  </main>
</template>

<style scoped>
/* Mantenemos la consistencia visual */
.login-shell { 
  min-height: 100vh; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  background: radial-gradient(circle at top left, rgba(47, 115, 224, 0.22), transparent 30%), linear-gradient(180deg, #f6f9fe 0%, #eef3fb 100%); 
}
.login-form-panel { 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  padding: 2rem; 
}
.login-card { 
  width: 100%; 
  max-width: 480px; 
  background: rgba(255, 255, 255, 0.96); 
  border: 1px solid rgba(15, 23, 42, 0.08); 
  border-radius: 1.75rem; 
  padding: 2.5rem 2rem; 
}
</style>