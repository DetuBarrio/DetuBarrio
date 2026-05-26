<script setup>
import { ref } from 'vue'
import { RouterLink } from 'vue-router'

const email = ref('')
const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

async function handleRequestReset() {
  loading.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    const response = await fetch('/api/auth/forgot-password', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: email.value.trim() }),
    })

    const data = await response.json().catch(() => null)

    if (!response.ok) {
      throw new Error(data?.error || 'No se pudo procesar la solicitud')
    }

    // Mostramos el mensaje de éxito que manda el backend
    successMessage.value = data?.message || 'Si el correo existe, se ha enviado un enlace de recuperación.'
    email.value = '' // Limpiamos el input
  } catch (error) {
    console.error('Error al solicitar recuperación:', error)
    errorMessage.value = error.message || 'Ocurrió un error inesperado. Inténtalo de nuevo.'
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
            <p class="text-uppercase text-muted small mb-1">Seguridad de la cuenta</p>
            <h2 class="h3 fw-bold mb-0">Recuperar contraseña</h2>
          </div>
          <RouterLink class="btn btn-outline-secondary btn-sm" to="/login">Volver</RouterLink>
        </div>

        <p class="text-muted small mb-4">
          Introduce tu dirección de correo electrónico y te enviaremos un enlace seguro para restablecer tu contraseña.
        </p>

        <div v-if="errorMessage" class="alert alert-danger border-0 mb-3">{{ errorMessage }}</div>
        <div v-if="successMessage" class="alert alert-success border-0 mb-3">{{ successMessage }}</div>

        <form v-if="!successMessage" @submit.prevent="handleRequestReset">
          <div class="mb-4">
            <label class="form-label">Correo electrónico</label>
            <input 
              v-model="email" 
              type="email" 
              class="form-control form-control-lg" 
              placeholder="tu.email@ejemplo.com" 
              required
              :disabled="loading"
            >
          </div>

          <button class="btn btn-primary w-100 btn-lg fw-bold" type="submit" :disabled="loading">
            {{ loading ? 'Enviando enlace...' : 'Enviar enlace de recuperación' }}
          </button>
        </form>

        <div v-else class="text-center mt-2">
          <RouterLink to="/login" class="btn btn-primary btn-lg w-100 fw-bold">
            Volver al inicio de sesión
          </RouterLink>
        </div>

      </div>
    </section>
  </main>
</template>

<style scoped>
/* Copiamos vuestra estructura base para que mantenga exactamente el mismo fondo estético */
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