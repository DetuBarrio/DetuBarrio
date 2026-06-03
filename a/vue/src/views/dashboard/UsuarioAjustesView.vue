<script setup>
import { computed, onMounted, ref } from 'vue'
import { clearAuth, getAuth } from '../../services/authService'
import { updateProfile, changePassword, deleteAccount } from '../../services/userService'
import { useRouter } from 'vue-router'

const router = useRouter()

const auth = ref(getAuth())

const profileForm = ref({ nombre: '', email: '' })
const profileSaving = ref(false)
const profileSuccess = ref('')
const profileError = ref('')

const passForm = ref({ contrasenaActual: '', nuevaContrasena: '', confirmarContrasena: '' })
const passSaving = ref(false)
const passSuccess = ref('')
const passError = ref('')
const passVisible = ref({ actual: false, nueva: false, confirm: false })

const deleting = ref(false)
const deleteError = ref('')

async function handleDeleteAccount() {
  deleteError.value = ''
  deleting.value = true
  try {
    await deleteAccount()
    clearAuth()
    router.push({ name: 'home' })
  } catch (error) {
    deleteError.value = error?.response?.data?.error || 'Error al eliminar la cuenta'
    deleting.value = false
  }
}

onMounted(() => {
  if (auth.value) {
    profileForm.value.nombre = auth.value.nombre || ''
    profileForm.value.email = auth.value.email || ''
  }
})

async function handleUpdateProfile() {
  profileSaving.value = true
  profileSuccess.value = ''
  profileError.value = ''
  try {
    await updateProfile(profileForm.value.nombre, profileForm.value.email)
    profileSuccess.value = 'Perfil actualizado correctamente'
    auth.value = getAuth()
  } catch (error) {
    profileError.value = error?.response?.data?.error || 'Error al actualizar el perfil'
  } finally {
    profileSaving.value = false
  }
}

async function handleChangePassword() {
  passSuccess.value = ''
  passError.value = ''
  if (passForm.value.nuevaContrasena !== passForm.value.confirmarContrasena) {
    passError.value = 'Las contraseñas no coinciden'
    return
  }
  if (passForm.value.nuevaContrasena.length < 6) {
    passError.value = 'La nueva contraseña debe tener al menos 6 caracteres'
    return
  }
  passSaving.value = true
  try {
    await changePassword(
      passForm.value.contrasenaActual,
      passForm.value.nuevaContrasena,
      passForm.value.confirmarContrasena,
    )
    passSuccess.value = 'Contraseña cambiada correctamente'
    passForm.value = { contrasenaActual: '', nuevaContrasena: '', confirmarContrasena: '' }
  } catch (error) {
    passError.value = error?.response?.data?.error || 'Error al cambiar la contraseña'
  } finally {
    passSaving.value = false
  }
}

function togglePass(field) {
  passVisible.value[field] = !passVisible.value[field]
}
</script>

<template>
  <div class="settings-page">
    <div class="settings-header">
      <div>
        <p class="eyebrow mb-1">Área personal</p>
        <h1 class="h3 fw-bold mb-1">Ajustes</h1>
        <p class="text-muted mb-0">Gestiona tu perfil, seguridad y preferencias de la cuenta.</p>
      </div>
    </div>

    <div class="row g-4">
      <div class="col-lg-6">
        <div class="settings-card">
          <div class="settings-card__head">
            <h5 class="fw-bold mb-0"><i class="bi bi-person me-2"></i>Perfil</h5>
            <span class="text-muted small">Información personal</span>
          </div>

          <div v-if="profileSuccess" class="alert alert-success border-0 shadow-sm rounded-3 mb-3">
            <i class="bi bi-check-circle-fill me-1"></i> {{ profileSuccess }}
          </div>
          <div v-if="profileError" class="alert alert-danger border-0 shadow-sm rounded-3 mb-3">
            <i class="bi bi-exclamation-triangle-fill me-1"></i> {{ profileError }}
          </div>

          <form @submit.prevent="handleUpdateProfile">
            <div class="mb-3">
              <label class="form-label fw-semibold small text-muted">Nombre completo</label>
              <input
                v-model="profileForm.nombre"
                type="text"
                class="form-control form-control-lg"
                placeholder="Tu nombre"
                required
              />
            </div>
            <div class="mb-4">
              <label class="form-label fw-semibold small text-muted">Correo electrónico</label>
              <input
                v-model="profileForm.email"
                type="email"
                class="form-control form-control-lg"
                placeholder="tu@email.com"
                required
              />
            </div>
            <button type="submit" class="btn btn-primary w-100 fw-bold py-2" :disabled="profileSaving">
              <span v-if="profileSaving" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else class="bi bi-check-lg me-1"></i>
              {{ profileSaving ? 'Guardando...' : 'Guardar cambios' }}
            </button>
          </form>
        </div>
      </div>

      <div class="col-lg-6">
        <div class="settings-card">
          <div class="settings-card__head">
            <h5 class="fw-bold mb-0"><i class="bi bi-shield-lock me-2"></i>Seguridad</h5>
            <span class="text-muted small">Cambiar contraseña</span>
          </div>

          <div v-if="passSuccess" class="alert alert-success border-0 shadow-sm rounded-3 mb-3">
            <i class="bi bi-check-circle-fill me-1"></i> {{ passSuccess }}
          </div>
          <div v-if="passError" class="alert alert-danger border-0 shadow-sm rounded-3 mb-3">
            <i class="bi bi-exclamation-triangle-fill me-1"></i> {{ passError }}
          </div>

          <form @submit.prevent="handleChangePassword">
            <div class="mb-3">
              <label class="form-label fw-semibold small text-muted">Contraseña actual</label>
              <div class="input-group">
                <input
                  v-model="passForm.contrasenaActual"
                  :type="passVisible.actual ? 'text' : 'password'"
                  class="form-control form-control-lg"
                  placeholder="••••••••"
                  required
                />
                <button class="btn btn-outline-secondary" type="button" @click="togglePass('actual')">
                  <i :class="passVisible.actual ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
                </button>
              </div>
            </div>
            <div class="mb-3">
              <label class="form-label fw-semibold small text-muted">Nueva contraseña</label>
              <div class="input-group">
                <input
                  v-model="passForm.nuevaContrasena"
                  :type="passVisible.nueva ? 'text' : 'password'"
                  class="form-control form-control-lg"
                  placeholder="Mínimo 6 caracteres"
                  required
                />
                <button class="btn btn-outline-secondary" type="button" @click="togglePass('nueva')">
                  <i :class="passVisible.nueva ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
                </button>
              </div>
            </div>
            <div class="mb-4">
              <label class="form-label fw-semibold small text-muted">Confirmar nueva contraseña</label>
              <div class="input-group">
                <input
                  v-model="passForm.confirmarContrasena"
                  :type="passVisible.confirm ? 'text' : 'password'"
                  class="form-control form-control-lg"
                  placeholder="Repite la nueva contraseña"
                  required
                />
                <button class="btn btn-outline-secondary" type="button" @click="togglePass('confirm')">
                  <i :class="passVisible.confirm ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
                </button>
              </div>
            </div>
            <button type="submit" class="btn btn-primary w-100 fw-bold py-2" :disabled="passSaving">
              <span v-if="passSaving" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else class="bi bi-key me-1"></i>
              {{ passSaving ? 'Cambiando...' : 'Cambiar contraseña' }}
            </button>
          </form>
        </div>
      </div>
    </div>

    <div class="row g-4 mt-2">
      <div class="col-12">
        <div class="settings-card border-danger">
          <div class="settings-card__head">
            <h5 class="fw-bold mb-0 text-danger"><i class="bi bi-exclamation-triangle me-2"></i>Zona peligrosa</h5>
            <span class="text-danger small">Eliminación de cuenta</span>
          </div>
          <p class="text-muted small mb-3">
            Una vez que elimines tu cuenta, no hay vuelta atrás. Todos tus datos asociados se eliminarán permanentemente.
          </p>
          <div v-if="deleteError" class="alert alert-danger border-0 shadow-sm rounded-3 mb-3">
            <i class="bi bi-exclamation-triangle-fill me-1"></i> {{ deleteError }}
          </div>
          <button class="btn btn-outline-danger fw-bold px-4" :disabled="deleting" @click="deleting = true">
            <i class="bi bi-trash3 me-1"></i> Eliminar mi cuenta
          </button>
          <div v-if="deleting" class="mt-3 p-3 bg-danger-light rounded-3 border border-danger">
            <p class="mb-2 fw-semibold text-danger small">¿Estás seguro? Esta acción no se puede deshacer. Se eliminarán tus datos personales, reservas y toda la información asociada.</p>
            <div class="d-flex gap-2">
              <button class="btn btn-danger btn-sm fw-bold" :disabled="deleting" @click="handleDeleteAccount">
                <span v-if="deleting" class="spinner-border spinner-border-sm me-1"></span>
                Sí, eliminar cuenta
              </button>
              <button class="btn btn-outline-secondary btn-sm fw-bold" :disabled="deleting" @click="deleting = false">
                Cancelar
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.settings-page {
  max-width: 960px;
  margin: 0 auto;
}

.settings-header {
  margin-bottom: 1.5rem;
}

.eyebrow {
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--db-primary, #025197);
}

.settings-card {
  background: #ffffff;
  border-radius: 1.25rem;
  border: 1px solid rgba(15, 23, 42, 0.08);
  padding: 1.5rem;
  height: 100%;
}

.settings-card__head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.25rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid rgba(15, 23, 42, 0.06);
}

.form-control:focus {
  border-color: var(--db-primary, #025197);
  box-shadow: 0 0 0 0.2rem rgba(2, 81, 151, 0.15);
}

.btn-primary {
  background: var(--db-primary, #025197);
  border-color: var(--db-primary, #025197);
  border-radius: 0.75rem;
}

.btn-primary:hover {
  background: #014682;
  border-color: #014682;
}

.btn-outline-danger {
  border-radius: 0.75rem;
}

.bg-danger-light {
  background: rgba(239, 68, 68, 0.06);
}

.input-group .btn-outline-secondary {
  border-color: #dee2e6;
}

.input-group .btn-outline-secondary:hover {
  background: #f8f9fa;
}
</style>
