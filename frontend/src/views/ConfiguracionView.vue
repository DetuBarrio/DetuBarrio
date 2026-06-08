<template>
  <div class="config-page">
    <div class="page-header">
      <p class="eyebrow mb-1">Área de comercio</p>
      <h1 class="h3 fw-bold mb-1">Configuración</h1>
      <p class="text-muted mb-0">Gestiona la identidad visual y los datos de tu comercio.</p>
    </div>

    <div v-if="loadingComercio" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-3 text-muted mb-0 fw-medium">Cargando los datos de tu comercio...</p>
    </div>

    <form v-else @submit.prevent="guardarDatosGenerales" class="config-form">
      <div class="row g-4">
        <div class="col-12">
          <div class="config-card">
            <div class="config-card__head">
              <h5 class="fw-bold mb-0"><i class="bi bi-image me-2"></i>Identidad visual</h5>
              <span class="text-muted small">Logo y banner</span>
            </div>

            <div class="row g-4">
              <div class="col-md-6">
                <label class="form-label fw-semibold small text-muted">Logo del comercio</label>
                <input type="file" @change="onFileSelected($event, 'logo')" accept="image/*" class="form-control" />
                <small class="text-muted d-block mt-1">Recomendado: 500×500px · PNG, JPG</small>
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold small text-muted">Banner principal</label>
                <input type="file" @change="onFileSelected($event, 'banner')" accept="image/*" class="form-control" />
                <small class="text-muted d-block mt-1">Formatos: PNG, JPG</small>
              </div>
            </div>
          </div>
        </div>

        <div class="col-12">
          <div class="config-card">
            <div class="config-card__head">
              <h5 class="fw-bold mb-0"><i class="bi bi-info-circle me-2"></i>Información del establecimiento</h5>
              <span class="text-muted small">Datos generales</span>
            </div>

            <div class="row g-4">
              <div class="col-md-6">
                <label class="form-label fw-semibold small text-muted">Nombre del negocio</label>
                <input v-model="comercio.nombreComercio" type="text" class="form-control form-control-lg" placeholder="Ej: Chucherías Paqui" required />
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold small text-muted">📍 Ubicación / Dirección</label>
                <input v-model="comercio.ubicacion" type="text" class="form-control form-control-lg" placeholder="Calle del Pan, 123, 28080 Madrid" />
              </div>
              <div class="col-12">
                <label class="form-label fw-semibold small text-muted">Descripción</label>
                <textarea v-model="comercio.descripcion" rows="4" class="form-control form-control-lg" placeholder="Cuéntales a tus vecinos qué haces especial..."></textarea>
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold small text-muted">Horario comercial</label>
                <input v-model="comercio.horario" type="text" class="form-control form-control-lg" placeholder="Ej: 09:00 - 20:00" />
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold small text-muted">Días de apertura</label>
                <input v-model="comercio.diasApertura" type="text" class="form-control form-control-lg" placeholder="Ej: Lunes a Sábado" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="d-flex justify-content-between align-items-center mt-4">
        <button type="button" class="btn btn-outline-secondary fw-bold rounded-pill px-4" @click="irAlDashboard">
          <i class="bi bi-arrow-left me-1"></i> Cancelar y salir
        </button>
        <button type="submit" class="btn btn-primary fw-bold rounded-pill px-4 py-2" :disabled="loading">
          <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
          <i v-else class="bi bi-check-lg me-1"></i>
          {{ loading ? 'Guardando...' : 'Guardar cambios' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { apiUrl } from '../config/api'
import { showToast } from '../utils/toastService'

const router = useRouter()

const comercio = ref({
  id: null,
  nombreComercio: '',
  ubicacion: '',
  descripcion: '',
  horario: '',
  diasApertura: '',
  logo: '',
  banner: '',
  categoriaId: null,
})

const logoFile = ref(null)
const bannerFile = ref(null)
const loading = ref(false)
const loadingComercio = ref(true)

onMounted(async () => {
  const usuarioId = localStorage.getItem('usuarioId')
  const backupComercioId = localStorage.getItem('comercioId')

  if (!usuarioId) {
    console.warn('No hay usuarioId en localStorage')
    loadingComercio.value = false
    return
  }

  try {
    const response = await axios.get(apiUrl(`/api/comercios/usuario/${usuarioId}`))
    if (response.data) {
      comercio.value = response.data
      if (response.data.nombre && !response.data.nombreComercio) {
        comercio.value.nombreComercio = response.data.nombre
      }
    }
  } catch (error) {
    console.error('Error cargando comercio:', error)
    if (backupComercioId) {
      comercio.value.id = Number(backupComercioId)
    }
  } finally {
    loadingComercio.value = false
  }
})

function irAlDashboard() {
  router.push('/dashboard/comercio')
}

function onFileSelected(event, type) {
  const file = event.target.files[0]
  if (!file) return
  if (type === 'logo') {
    logoFile.value = file
  } else {
    bannerFile.value = file
  }
}

async function guardarDatosGenerales() {
  if (!comercio.value.id) {
    const ultimoRecursoId = localStorage.getItem('comercioId')
    if (ultimoRecursoId) {
      comercio.value.id = Number(ultimoRecursoId)
    } else {
      showToast('Error: No se ha podido vincular un ID de comercio válido.', 'error')
      return
    }
  }

  loading.value = true
  try {
    const formData = new FormData()
    formData.append('nombre', comercio.value.nombreComercio || '')
    formData.append('ubicacion', comercio.value.ubicacion || '')
    formData.append('descripcion', comercio.value.descripcion || '')
    formData.append('horario', comercio.value.horario || '')
    formData.append('diasApertura', comercio.value.diasApertura || '')

    if (logoFile.value instanceof File) {
      formData.append('logo', logoFile.value)
    }
    if (bannerFile.value instanceof File) {
      formData.append('banner', bannerFile.value)
    }

    const response = await axios.put(apiUrl(`/api/comercios/${comercio.value.id}/fotos`), formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })

    if (response.data) {
      comercio.value = response.data
      if (response.data.nombre) {
        comercio.value.nombreComercio = response.data.nombre
      }
    }

    showToast('Perfil actualizado con éxito', 'success')
    irAlDashboard()
  } catch (error) {
    console.error('Error al guardar:', error)
    const mensajeError = error.response?.data?.message || 'Error al actualizar el perfil'
    showToast(`Error: ${mensajeError}`, 'error')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.config-page {
  width: 100%;
}

.page-header {
  margin-bottom: 1.5rem;
}

.eyebrow {
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--db-primary, #025197);
}

.config-card {
  background: #ffffff;
  border-radius: 1.25rem;
  border: 1px solid rgba(15, 23, 42, 0.08);
  padding: 1.5rem;
}

.config-card__head {
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

.form-control-lg {
  border-radius: 0.75rem;
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

.btn-outline-secondary {
  border-radius: 0.75rem;
}
</style>
