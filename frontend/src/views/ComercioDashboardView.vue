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
    default: return 'bg-warning text-dark'
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
    <aside class="dashboard-sidebar shadow-sm">
      <div class="profile-card border-0 shadow-xs">
        <div class="profile-avatar shadow-sm">{{ initial }}</div>
        <div class="overflow-hidden">
          <h6 class="mb-0 fw-bold text-dark text-truncate">{{ comercioName }}</h6>
          <small class="text-muted text-truncate d-block mt-0.5">{{ email }}</small>
        </div>
      </div>

      <nav class="sidebar-nav mt-3">
        <RouterLink to="/dashboard/comercio" active-class="active">
          <i class="bi bi-grid-1x2-fill me-2.5"></i> Panel general
        </RouterLink>
        
        <RouterLink to="/dashboard/comercio/reservas" active-class="active">
          <i class="bi bi-calendar-check-fill me-2.5"></i> Reservas
        </RouterLink>

        <RouterLink to="/dashboard/disponibilidad" active-class="active">
          <i class="bi bi-clock-fill me-2.5"></i> Horarios
        </RouterLink>

        <RouterLink to="/dashboard/comercio/clientes" active-class="active">
          <i class="bi bi-people-fill me-2.5"></i> Clientes
        </RouterLink>
        
        <RouterLink to="/dashboard/configuracion" active-class="active">
          <i class="bi bi-gear-fill me-2.5"></i> Configuración
        </RouterLink>
      </nav>

      <div class="sidebar-actions mt-auto border-top pt-3">
        <button class="btn btn-logout-custom w-100 fw-bold rounded-3 py-2" type="button" @click="handleLogout">
          <i class="bi bi-box-arrow-left me-2"></i> Cerrar sesión
        </button>
      </div>
    </aside>

    <section class="dashboard-content">
      <template v-if="route.path === '/dashboard/comercio'">
        <header class="dashboard-header align-items-center mb-4">
          <div>
            <p class="eyebrow mb-1 text-primary">Área de comercio</p>
            <h1 class="h3 fw-extrabold text-dark mb-1">Panel general</h1>
            <p class="text-muted mb-0 small">Bienvenido de nuevo si quiere. Aquí tienes un resumen operativo de tu local.</p>
          </div>

          <div class="header-actions d-flex align-items-center gap-3">
            <RouterLink class="btn btn-escaparate-top rounded-pill px-3.5 py-2 fw-bold shadow-sm small" to="/comercios">
              <i class="bi bi-eye-fill me-1.5"></i> Ver escaparate
            </RouterLink>

            <button class="icon-round shadow-xs position-relative" type="button" aria-label="Notificaciones">
              <i class="bi bi-bell-fill text-secondary"></i>
              <span class="position-absolute top-2 start-2 translate-middle p-1.5 bg-danger border border-light rounded-circle"></span>
            </button>
            <div class="avatar-badge shadow-sm">{{ initial }}</div>
          </div>
        </header>

        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
          <p class="mt-3 text-muted mb-0 fw-medium">Cargando tu panel...</p>
        </div>

        <template v-else>
          <div v-if="errorMessage" class="alert alert-danger border-0 shadow-sm mb-4 rounded-3">{{ errorMessage }}</div>

          <div v-if="shouldShowStatusNotice && !notificationDismissed" class="alert border-0 shadow-sm mb-4 rounded-4" :class="{
            'alert-info bg-info-light text-info-emphasis': estadoComercio === 'PENDIENTE',
            'alert-success bg-success-light text-success-emphasis': estadoComercio === 'APROBADO',
            'alert-danger bg-danger-light text-danger-emphasis': estadoComercio === 'RECHAZADO'
          }">
            <div class="d-flex align-items-center justify-content-between flex-wrap gap-3 p-2">
              <div class="d-flex align-items-center gap-3">
                <i class="bi bi-info-circle-fill fs-3"></i>
                <div>
                  <h5 class="mb-0.5 fw-bold fs-6">Estado del comercio</h5>
                  <p class="mb-0 small">Tu establecimiento se encuentra actualmente: <strong class="text-uppercase">{{ estadoText }}</strong></p>
                </div>
              </div>
              <div class="d-flex align-items-center gap-2">
                <span class="badge rounded-pill px-3 py-1.5 fw-bold shadow-xs" :class="estadoBadgeClass">{{ estadoText }}</span>
                <button class="btn btn-sm btn-white-shadow rounded-3 font-semibold" type="button" @click="handleDismissNotification" v-if="canDismissNotification && estadoComercio !== 'PENDIENTE'">
                  Entendido
                </button>
              </div>
            </div>
          </div>

          <div class="hero-panel card border-0 shadow-sm mb-4 overflow-hidden rounded-4">
            <div class="row g-0 align-items-center">
              <div class="col-md-7 col-lg-8">
                <div class="hero-panel__text p-4 p-lg-5">
                  <p class="eyebrow mb-2 text-primary tracking-wider">Operaciones</p>
                  <h2 class="fw-extrabold text-dark display-6 mb-2 fs-3 leading-tight">Controla tu negocio al instante</h2>
                  <p class="text-secondary mb-4 small max-width-p leading-relaxed">Organiza las citas de tus clientes, actualiza tus franjas horarias libres y cambia datos generales de tu local.</p>
                  
                  <button class="btn btn-primary px-4 py-2.5 fw-bold rounded-3 shadow-sm btn-hero-action" type="button" @click="abrirGestionHoras">
                    <i class="bi bi-sliders me-1.5"></i> Configurar Disponibilidad
                  </button>
                </div>
              </div>
              <div class="col-md-5 col-lg-4 d-none d-md-block h-100">
                <div class="hero-panel__image position-relative h-100">
                  <img :src="fondo" alt="Imagen de negocio" class="w-100 h-100 object-fit-cover" />
                  <div class="hero-image-overlay"></div>
                </div>
              </div>
            </div>
          </div>

          <div class="row g-4 mb-4">
            <div class="col-md-6">
              <div class="metric-card-custom p-4 bg-white rounded-4 shadow-sm d-flex align-items-center justify-content-between border-custom-light hover-shadow-metric transition-all">
                <div>
                  <p class="text-uppercase tracking-wider small text-muted fw-bold mb-1 fs-7">Reservas para Hoy</p>
                  <div class="metric-value-custom text-dark fw-extrabold display-5 leading-none">8</div>
                  <span class="badge bg-success-light text-success rounded-pill px-2.5 py-1 small fw-bold mt-1.5 d-inline-block shadow-xs">
                    <i class="bi bi-check-circle-fill me-1"></i> Al día
                  </span>
                </div>
                <div class="metric-icon-box bg-primary-light text-primary rounded-4 p-3.5">
                  <i class="bi bi-calendar2-week-fill fs-3"></i>
                </div>
              </div>
            </div>
            
            <div class="col-md-6">
              <div class="metric-card-custom p-4 bg-white rounded-4 shadow-sm d-flex align-items-center justify-content-between border-custom-light hover-shadow-metric transition-all">
                <div>
                  <p class="text-uppercase tracking-wider small text-muted fw-bold mb-1 fs-7">Opiniones del Barrio</p>
                  <div class="metric-value-custom text-dark fw-extrabold display-5 leading-none">4</div>
                  <span class="badge bg-warning-light text-warning-emphasis rounded-pill px-2.5 py-1 small fw-bold mt-1.5 d-inline-block shadow-xs">
                    <i class="bi bi-star-fill text-warning me-1"></i> Clientes activos
                  </span>
                </div>
                <div class="metric-icon-box bg-secondary-light text-secondary-emphasis rounded-4 p-3.5">
                  <i class="bi bi-chat-heart-fill fs-3"></i>
                </div>
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
/* Contenedor Base */
.dashboard-shell {
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  min-height: calc(100vh - 72px);
  background: #f8fafc;
}

/* Sidebar Rediseñada */
.dashboard-sidebar {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  padding: 1.75rem 1.25rem;
  background: #ffffff;
  border-right: 1px solid #e2e8f0;
}

.profile-card {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.85rem;
  border-radius: 12px;
  background-color: #f1f5f9;
}

.profile-avatar, .avatar-badge {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #3a86ff 0%, #2563eb 100%);
  color: #ffffff;
  font-weight: 700;
  flex-shrink: 0;
}

/* Enlaces del Navegador Lateral */
.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}
.sidebar-nav a {
  text-decoration: none;
  color: #64748b;
  padding: 0.75rem 1rem;
  border-radius: 10px;
  font-weight: 500;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
}
.sidebar-nav a:hover {
  background: #f1f5f9;
  color: #1e293b;
}
.sidebar-nav a.active {
  background: rgba(58, 134, 255, 0.08);
  color: #3a86ff;
  font-weight: 700;
}

/* Botón salir */
.btn-logout-custom {
  background: transparent;
  color: #ef4444;
  border: 1px solid rgba(239, 68, 68, 0.15);
  transition: all 0.2s;
}
.btn-logout-custom:hover {
  background: #fef2f2;
  border-color: #ef4444;
}

/* Contenido */
.dashboard-content {
  padding: 2.25rem;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
}

.fw-extrabold { font-weight: 800; }
.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 0.75rem;
  font-weight: 700;
}

/* Botón Escaparate Superior */
.btn-escaparate-top {
  background-color: #ffffff;
  color: #475569;
  border: 1px solid #e2e8f0;
  transition: all 0.2s;
}
.btn-escaparate-top:hover {
  background-color: #f8fafc;
  color: #0f172a;
  transform: translateY(-1px);
}

.icon-round {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}
.icon-round:hover {
  background-color: #f8fafc;
}

/* Hero Panel Tarjeta */
.hero-panel {
  background: #ffffff;
  border: 1px solid #e2e8f0 !important;
}
.btn-hero-action {
  background: linear-gradient(135deg, #3a86ff 0%, #2563eb 100%);
  border: none;
  transition: all 0.2s;
}
.btn-hero-action:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
}
.max-width-p {
  max-width: 520px;
}
.hero-image-overlay {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: linear-gradient(90deg, #ffffff 0%, transparent 100%);
}

/* Tarjetas de Métricas Premium */
.metric-card-custom {
  background: #ffffff;
}
.hover-shadow-metric:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0,0,0,0.04) !important;
}
.border-custom-light {
  border: 1px solid #e2e8f0 !important;
}
.metric-icon-box {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
}

/* Variaciones de fondos ligeros */
.bg-primary-light { background-color: rgba(58, 134, 255, 0.08); }
.bg-success-light { background-color: rgba(16, 185, 129, 0.08); }
.bg-warning-light { background-color: rgba(245, 158, 11, 0.08); }
.bg-secondary-light { background-color: rgba(100, 116, 139, 0.08); }
.bg-info-light { background-color: rgba(6, 182, 212, 0.08); }
.bg-danger-light { background-color: rgba(239, 68, 68, 0.08); }

.btn-white-shadow {
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 4px rgba(0,0,0,0.04);
  font-weight: 600;
  color: #334155;
}

.text-neutral-emphasis { color: #475569; }
.tracking-wider { letter-spacing: 0.1em; }
.leading-none { line-height: 1; }
.leading-tight { line-height: 1.25; }
.leading-relaxed { line-height: 1.625; }

.fs-7 { font-size: 0.85rem; }

.shadow-xs { box-shadow: 0 1px 2px rgba(0,0,0,0.03); }
.transition-all { transition: all 0.2s ease; }
</style>