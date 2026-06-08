<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import fondo from '../../assets/images/fondo.png'
import { getAuth } from '../../services/authService'
import { fetchComercioDashboard } from '../../services/dashboardService'

const router = useRouter()
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
  router.push({ name: 'dashboard-comercio-disponibilidad' })
}

function irAReservas() {
  router.push({ name: 'dashboard-comercio-reservas' })
}

function irAClientes() {
  router.push({ name: 'dashboard-comercio-clientes' })
}

async function loadDashboard() {
  loading.value = true
  errorMessage.value = ''
  try {
    dashboard.value = await fetchComercioDashboard()
  } catch (error) {
    errorMessage.value = error?.response?.data?.details?.[0] || error?.message || 'No se pudo cargar el panel de comercio.'
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadDashboard()
})

function handleDismissNotification() {
  notificationDismissed.value = true
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  const d = new Date(dateStr + 'T00:00:00')
  return d.toLocaleDateString('es-ES', { day: 'numeric', month: 'short', year: 'numeric' })
}

function reservaBadgeClass(estado) {
  switch (estado) {
    case 'CONFIRMADA': return 'status-confirmada'
    case 'CANCELADA': return 'status-cancelada'
    default: return 'status-pendiente'
  }
}

function estadoLabel(estado) {
  switch (estado) {
    case 'CONFIRMADA': return 'Confirmada'
    case 'CANCELADA': return 'Cancelada'
    default: return 'Pendiente'
  }
}
</script>

<template>
  <div>
    <header class="dashboard-header align-items-center mb-4">
      <div>
        <p class="eyebrow mb-1 text-primary">Área de comercio</p>
        <h1 class="h3 fw-extrabold text-dark mb-1">Panel general</h1>
        <p class="text-muted mb-0 small">Bienvenido de nuevo. Aquí tienes un resumen operativo de tu local.</p>
      </div>

      <div class="header-actions d-flex align-items-center gap-3">
        <RouterLink class="btn btn-escaparate-top rounded-pill px-3 py-2 fw-bold shadow-sm small" to="/comercios">
          <i class="bi bi-eye-fill me-1"></i> Ver escaparate
        </RouterLink>

        <button class="icon-round shadow-xs position-relative" type="button" aria-label="Notificaciones">
          <i class="bi bi-bell-fill text-secondary"></i>
          <span class="position-absolute top-2 start-2 translate-middle p-1 bg-danger border border-light rounded-circle"></span>
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
              <h5 class="mb-0 fw-bold fs-6">Estado del comercio</h5>
              <p class="mb-0 small">Tu establecimiento se encuentra actualmente: <strong class="text-uppercase">{{ estadoText }}</strong></p>
            </div>
          </div>
          <div class="d-flex align-items-center gap-2">
            <span class="badge rounded-pill px-3 py-1 fw-bold shadow-xs" :class="estadoBadgeClass">{{ estadoText }}</span>
            <button class="btn btn-sm btn-white-shadow rounded-3 fw-semibold" type="button" @click="handleDismissNotification" v-if="canDismissNotification && estadoComercio !== 'PENDIENTE'">
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
                <i class="bi bi-sliders me-1"></i> Configurar Disponibilidad
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
        <div class="col-md-4">
          <div class="metric-card-custom p-4 bg-white rounded-4 shadow-sm d-flex align-items-center justify-content-between border-custom-light hover-shadow-metric transition-all" style="cursor: pointer;" @click="irAReservas">
            <div>
              <p class="text-uppercase tracking-wider small text-muted fw-bold mb-1 fs-7">Reservas para Hoy</p>
              <div class="metric-value-custom text-dark fw-extrabold display-5 leading-none">{{ dashboard?.reservasHoy ?? 0 }}</div>
              <span class="badge bg-success-light text-success rounded-pill px-2 py-1 small fw-bold mt-1 d-inline-block shadow-xs">
                <i class="bi bi-check-circle-fill me-1"></i> Ver reservas
              </span>
            </div>
            <div class="metric-icon-box bg-primary-light text-primary rounded-4 p-3">
              <i class="bi bi-calendar2-week-fill fs-3"></i>
            </div>
          </div>
        </div>

        <div class="col-md-4">
          <div class="metric-card-custom p-4 bg-white rounded-4 shadow-sm d-flex align-items-center justify-content-between border-custom-light hover-shadow-metric transition-all" style="cursor: pointer;" @click="irAReservas">
            <div>
              <p class="text-uppercase tracking-wider small text-muted fw-bold mb-1 fs-7">Reservas Activas</p>
              <div class="metric-value-custom text-dark fw-extrabold display-5 leading-none">{{ dashboard?.reservasActivas ?? 0 }}</div>
              <span class="badge bg-info-light text-info-emphasis rounded-pill px-2 py-1 small fw-bold mt-1 d-inline-block shadow-xs">
                <i class="bi bi-check2-circle me-1"></i> Gestionar
              </span>
            </div>
            <div class="metric-icon-box bg-primary-light text-primary rounded-4 p-3">
              <i class="bi bi-calendar-check-fill fs-3"></i>
            </div>
          </div>
        </div>

        <div class="col-md-4">
          <div class="metric-card-custom p-4 bg-white rounded-4 shadow-sm d-flex align-items-center justify-content-between border-custom-light hover-shadow-metric transition-all" style="cursor: pointer;" @click="irAClientes">
            <div>
              <p class="text-uppercase tracking-wider small text-muted fw-bold mb-1 fs-7">Opiniones del Barrio</p>
              <div class="metric-value-custom text-dark fw-extrabold display-5 leading-none">{{ dashboard?.totalResenas ?? 0 }}</div>
              <span class="badge bg-warning-light text-warning-emphasis rounded-pill px-2 py-1 small fw-bold mt-1 d-inline-block shadow-xs">
                <i class="bi bi-star-fill text-warning me-1"></i> {{ (dashboard?.mediaValoracion ?? 0).toFixed(1) }} / 5
              </span>
            </div>
            <div class="metric-icon-box bg-secondary-light text-secondary-emphasis rounded-4 p-3">
              <i class="bi bi-chat-heart-fill fs-3"></i>
            </div>
          </div>
        </div>
      </div>

      <div class="row g-4">
        <div class="col-lg-8">
          <div class="panel-card shadow-sm h-100">
            <div class="panel-card__head">
              <h5 class="fw-bold mb-0">Últimas reservas</h5>
              <button class="btn btn-sm btn-outline-primary fw-semibold rounded-pill" @click="irAReservas">
                Ver todas <i class="bi bi-arrow-right ms-1"></i>
              </button>
            </div>
            <div class="table-responsive">
              <table class="table align-middle mb-0">
                <thead>
                  <tr>
                    <th>Cliente</th>
                    <th>Fecha</th>
                    <th>Hora</th>
                    <th class="text-end">Estado</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="res in (dashboard?.ultimasReservas || [])" :key="res.id" style="cursor: pointer;" @click="irAReservas">
                    <td class="fw-semibold">{{ res.clienteNombre || 'Usuario #' + (res.idUsuario || res.id) }}</td>
                    <td>{{ formatDate(res.fechaReserva) }}</td>
                    <td>{{ res.horaInicio?.slice(0,5) || '-' }}</td>
                    <td class="text-end">
                      <span class="status-badge" :class="reservaBadgeClass(res.estadoReserva)">{{ estadoLabel(res.estadoReserva) }}</span>
                    </td>
                  </tr>
                  <tr v-if="!dashboard?.ultimasReservas?.length">
                    <td colspan="4" class="text-center text-muted py-3">No hay reservas recientes</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div class="col-lg-4">
          <div class="panel-card shadow-sm h-100">
            <div class="panel-card__head">
              <h5 class="fw-bold mb-0">Mi comercio</h5>
              <span class="text-muted small">Resumen</span>
            </div>
            <div class="d-grid gap-3">
              <div>
                <div class="text-muted small mb-1">Nombre</div>
                <div class="fw-semibold">{{ comercioName }}</div>
              </div>
              <div>
                <div class="text-muted small mb-1">Correo</div>
                <div class="fw-semibold">{{ email }}</div>
              </div>
              <div>
                <div class="text-muted small mb-1">Estado</div>
                <span class="badge rounded-pill px-3 py-1 fw-bold shadow-xs" :class="estadoBadgeClass">{{ estadoText }}</span>
              </div>
              <RouterLink class="btn btn-outline-primary fw-semibold" :to="{ name: 'dashboard-comercio-configuracion' }">
                <i class="bi bi-gear me-1"></i> Configuración
              </RouterLink>
              <RouterLink class="btn btn-outline-primary fw-semibold" :to="{ name: 'dashboard-comercio-disponibilidad' }">
                <i class="bi bi-sliders me-1"></i> Disponibilidad
              </RouterLink>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.dashboard-header {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.icon-round,
.avatar-badge {
  width: 46px;
  height: 46px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
  color: var(--db-primary);
  border: 1px solid rgba(47, 115, 224, 0.18);
}

.avatar-badge {
  background: var(--db-primary);
  color: #fff;
  font-weight: 700;
}

.eyebrow {
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.btn-escaparate-top {
  background: #ffffff;
  color: var(--db-primary);
  border: 1px solid rgba(47, 115, 224, 0.18);
}

.btn-escaparate-top:hover {
  background: #eef5ff;
  color: var(--db-primary);
}

.bg-info-light {
  background: #e0f2fe;
}

.bg-success-light {
  background: #dcfce7;
}

.bg-danger-light {
  background: #fee2e2;
}

.bg-warning-light {
  background: #fef9c3;
}

.text-info-emphasis {
  color: #075985;
}

.text-success-emphasis {
  color: #166534;
}

.text-danger-emphasis {
  color: #991b1b;
}

.text-warning-emphasis {
  color: #92400e;
}

.btn-white-shadow {
  background: #ffffff;
  color: #0f172a;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08);
}

.btn-white-shadow:hover {
  background: #f8fafc;
}

.hero-panel {
  overflow: hidden;
}

.hero-panel__image {
  position: relative;
  min-height: 100%;
}

.hero-panel__image img {
  min-height: 100%;
}

.hero-image-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, rgba(246, 249, 254, 0) 0%, rgba(246, 249, 254, 0.45) 100%);
}

.metric-card-custom {
  min-height: 140px;
}

.border-custom-light {
  border: 1px solid rgba(15, 23, 42, 0.08);
}

.metric-value-custom {
  font-size: 2.3rem;
}

.bg-primary-light {
  background: rgba(47, 115, 224, 0.12);
}

.bg-secondary-light {
  background: rgba(58, 134, 255, 0.12);
}

.text-primary-emphasis {
  color: var(--db-primary);
}

.metric-icon-box {
  width: 64px;
  height: 64px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.panel-card {
  background: #ffffff;
  border-radius: 1.25rem;
  border: 1px solid rgba(15, 23, 42, 0.08);
  padding: 1.25rem;
}

.panel-card__head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.35rem 0.8rem;
  border-radius: 999px;
  font-size: 0.76rem;
  font-weight: 700;
}

.status-confirmada {
  background: #dcfce7;
  color: #15803d;
}

.status-cancelada {
  background: #fee2e2;
  color: #b91c1c;
}

.status-pendiente {
  background: #fef9c3;
  color: #a16207;
}

@media (max-width: 992px) {
  .hero-panel__image img {
    min-height: 260px;
  }
}
</style>