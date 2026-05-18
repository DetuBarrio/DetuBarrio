<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRouter, useRoute } from 'vue-router'
import fondo from '../assets/images/fondo.png'
import { clearAuth, getAuth } from '../services/authService'
import { fetchComercioDashboard } from '../services/dashboardService'

const router = useRouter()
const route = useRoute()
const auth = ref(getAuth())
const loading = ref(false)
const errorMessage = ref('')
const dashboard = ref(null)
const notificationDismissed = ref(false)

const displayName = computed(() => dashboard.value?.nombre || auth.value?.nombre || 'Comercio')
const email = computed(() => dashboard.value?.email || auth.value?.email || '')
const comercioName = computed(() => dashboard.value?.comercioNombre || 'Mi comercio')
const estadoComercio = computed(() => dashboard.value?.estadoComercio || 'PENDIENTE')
const gestionAutorizada = computed(() => Boolean(dashboard.value?.gestionAutorizada))
const initial = computed(() => displayName.value.trim().charAt(0).toUpperCase() || 'C')

const notificationKey = computed(() => {
  const emailKey = (auth.value?.email || '').trim().toLowerCase() || 'sin-email'
  return `detubarrio_comercio_notice_${emailKey}`
})

const estadoBadgeClass = computed(() => {
  switch (estadoComercio.value) {
    case 'APROBADO': return 'bg-success'
    case 'RECHAZADO': return 'bg-danger'
    default: return 'bg-warning'
  }
})

const estadoText = computed(() => {
  switch (estadoComercio.value) {
    case 'APROBADO': return gestionAutorizada.value ? 'Aprobado y autorizado' : 'Cuenta aprobada'
    case 'RECHAZADO': return 'Rechazado'
    default: return 'Pendiente de aprobación'
  }
})

const shouldShowStatusNotice = computed(() => {
  if (estadoComercio.value === 'PENDIENTE') return true
  if (estadoComercio.value === 'APROBADO' && !gestionAutorizada.value) return true
  if (estadoComercio.value === 'RECHAZADO') return true
  return false
})

const canDismissNotification = computed(() => estadoComercio.value !== 'PENDIENTE')

function abrirGestionHoras() {
  router.push('/dashboard/disponibilidad')
}

function isNotificationDismissed() {
  return localStorage.getItem(notificationKey.value) === '1'
}

function setNotificationDismissed() {
  localStorage.setItem(notificationKey.value, '1')
}

function redirectByRole() {
  const currentAuth = getAuth()
  if (!currentAuth?.token) {
    router.replace({ name: 'login' })
    return false
  }
  if (currentAuth.rol !== 'COMERCIO') {
    router.replace(currentAuth.rol === 'USUARIO' ? { name: 'dashboard-usuario' } : currentAuth.rol === 'ADMIN' ? { name: 'admin' } : { name: 'home' })
    return false
  }
  auth.value = currentAuth
  return true
}

async function loadDashboard() {
  loading.value = true
  errorMessage.value = ''
  try {
    dashboard.value = await fetchComercioDashboard()
    notificationDismissed.value = isNotificationDismissed()
  } catch (error) {
    errorMessage.value = error?.response?.data?.details?.[0] || error?.message || 'No se pudo cargar el panel de comercio.'
  } finally {
    loading.value = false
  }
}

async function handleDismissNotification() {
  if (estadoComercio.value === 'RECHAZADO') {
    setNotificationDismissed()
    clearAuth()
    router.replace({ name: 'login' })
    return
  }
  setNotificationDismissed()
  notificationDismissed.value = true
}

function handleLogout() {
  clearAuth()
  router.replace({ name: 'login' })
}

onMounted(async () => {
  if (!redirectByRole()) return
  await loadDashboard()
})
</script>

<template>
  <main class="dashboard-shell">
    <aside class="dashboard-sidebar">
      <div class="profile-card">
        <div class="profile-avatar">{{ initial }}</div>
        <div>
          <h6 class="mb-1 fw-bold text-truncate" style="max-width: 150px;">{{ comercioName }}</h6>
          <small class="text-muted text-truncate d-block" style="max-width: 150px;">{{ email }}</small>
        </div>
      </div>

      <nav class="sidebar-nav">
        <RouterLink to="/dashboard/comercio" active-class="active">
          <i class="bi bi-grid me-2"></i> Panel general
        </RouterLink>
        
        <RouterLink to="/dashboard/comercio/reservas" active-class="active">
          <i class="bi bi-calendar-event me-2"></i> Reservas
        </RouterLink>

        <RouterLink to="/dashboard/disponibilidad" active-class="active">
          <i class="bi bi-clock-history me-2"></i> Horarios
        </RouterLink>

        <a href="#"><i class="bi bi-box me-2"></i> Productos</a>
        <a href="#"><i class="bi bi-people me-2"></i> Clientes</a>
        
        <RouterLink to="/dashboard/configuracion" active-class="active">
          <i class="bi bi-gear me-2"></i> Configuración
        </RouterLink>
      </nav>

      <div class="sidebar-actions">
        <RouterLink class="btn btn-light w-100 fw-semibold" to="/comercios">
          <i class="bi bi-shop me-1"></i> Ver escaparate
        </RouterLink>
        <button class="btn btn-outline-danger w-100 fw-semibold" type="button" @click="handleLogout">
          Cerrar sesión
        </button>
      </div>
    </aside>

    <section class="dashboard-content">
      <template v-if="route.path === '/dashboard/comercio'">
        <header class="dashboard-header">
          <div>
            <p class="eyebrow mb-1">Área de comercio</p>
            <h1 class="h2 fw-bold mb-1">Panel general - {{ comercioName }}</h1>
            <p class="text-muted mb-0">Bienvenido de nuevo, aquí tienes un resumen de tu actividad.</p>
          </div>

          <div class="header-actions">
            <button class="icon-round" type="button" aria-label="Notificaciones">
              <i class="bi bi-bell"></i>
            </button>
            <div class="avatar-badge">{{ initial }}</div>
          </div>
        </header>

        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
          <p class="mt-3 text-muted mb-0">Cargando tu panel...</p>
        </div>

        <template v-else>
          <div v-if="errorMessage" class="alert alert-danger border-0 shadow-sm mb-4">{{ errorMessage }}</div>

          <div v-if="shouldShowStatusNotice && !notificationDismissed" class="alert mb-4" :class="{
            'alert-info': estadoComercio === 'PENDIENTE',
            'alert-success': estadoComercio === 'APROBADO',
            'alert-danger': estadoComercio === 'RECHAZADO'
          }">
            <div class="d-flex align-items-start justify-content-between gap-3">
              <div>
                <h5 class="mb-1 fw-bold">Estado del comercio</h5>
                <p class="mb-0">Tu comercio está: <strong>{{ estadoText }}</strong></p>
              </div>
              <div class="d-flex flex-column align-items-end gap-2">
                <span class="badge" :class="estadoBadgeClass">{{ estadoText }}</span>
                <button class="btn btn-sm btn-light" type="button" @click="handleDismissNotification" v-if="canDismissNotification && estadoComercio !== 'PENDIENTE'">
                  Cerrar
                </button>
              </div>
            </div>
          </div>

          <div class="hero-panel shadow-sm mb-4">
            <div class="hero-panel__text">
              <p class="eyebrow mb-2">Resumen del negocio</p>
              <h2 class="fw-bold mb-2">Gestiona tu comercio desde una sola vista</h2>
              <p class="text-muted mb-4">Consulta productos, reservas y estadísticas sin salir del panel.</p>
              
              <button class="btn btn-primary px-4 py-2 fw-semibold" type="button" @click="abrirGestionHoras">
                <i class="bi bi-calendar-plus me-1"></i> Gestionar Disponibilidad
              </button>
            </div>
            <div class="hero-panel__image">
              <img :src="fondo" alt="Imagen de negocio" />
            </div>
          </div>

          <div class="row g-4 mb-4">
            <div class="col-md-4">
              <div class="metric-card shadow-sm">
                <p class="text-muted mb-2">Visitas al perfil</p>
                <div class="metric-value">1,230</div>
                <small class="text-success">+5.2%</small>
              </div>
            </div>
            <div class="col-md-4">
              <div class="metric-card shadow-sm">
                <p class="text-muted mb-2">Nuevas reservas</p>
                <div class="metric-value">8</div>
                <small class="text-success">Hoy</small>
              </div>
            </div>
            <div class="col-md-4">
              <div class="metric-card shadow-sm">
                <p class="text-muted mb-2">Opiniones</p>
                <div class="metric-value">4</div>
                <small class="text-danger">-0.5%</small>
              </div>
            </div>
          </div>
        </template>
      </template>

      <RouterView v-else />
    </section>
  </main>
</template>

<style scoped>
.dashboard-shell { display: grid; grid-template-columns: 280px minmax(0, 1fr); min-height: calc(100vh - 72px); background: #f6f9fe; }
.dashboard-sidebar { display: flex; flex-direction: column; gap: 1.25rem; padding: 1.5rem; background: white; border-right: 1px solid rgba(15, 23, 42, 0.08); }
.profile-card { display: flex; align-items: center; gap: 0.9rem; padding: 1rem; border-radius: 1rem; background: #f8fbff; overflow: hidden; }
.profile-avatar, .avatar-badge { min-width: 44px; height: 44px; border-radius: 999px; display: inline-flex; align-items: center; justify-content: center; background: #0d6efd; color: white; font-weight: 700; flex-shrink: 0; }
.sidebar-nav { display: flex; flex-direction: column; gap: 0.4rem; }
.sidebar-nav a { text-decoration: none; color: #495057; padding: 0.8rem 1rem; border-radius: 0.9rem; transition: all 0.2s; }
.sidebar-nav a.active, .sidebar-nav a:hover { background: #eaf2ff; color: #0d6efd; font-weight: 600; }
.sidebar-actions { margin-top: auto; display: grid; gap: 0.75rem; }
.dashboard-content { padding: 1.5rem; }
.dashboard-header { display: flex; align-items: center; justify-content: space-between; gap: 1rem; margin-bottom: 1.5rem; }
.eyebrow { text-transform: uppercase; letter-spacing: 0.18em; font-size: 0.72rem; color: #6c757d; font-weight: 700; }
.hero-panel { display: grid; grid-template-columns: 1.2fr 0.8fr; align-items: center; overflow: hidden; background: white; border-radius: 1.25rem; border: 1px solid rgba(15, 23, 42, 0.08); }
.hero-panel__text { padding: 1.75rem; }
.hero-panel__image img { width: 100%; height: 100%; object-fit: cover; min-height: 220px; }
.metric-card { background: white; padding: 1.25rem; border-radius: 1.25rem; border: 1px solid rgba(15, 23, 42, 0.08); }
.metric-value { font-size: 2rem; font-weight: 800; }
.icon-round { width: 40px; height: 40px; border-radius: 12px; border: 1px solid #e2e8f0; background: white; color: #64748b; display: flex; align-items: center; justify-content: center; }
</style>