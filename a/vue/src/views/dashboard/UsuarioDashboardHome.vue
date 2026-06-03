<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import callegestionCliente from '../../assets/images/callegestionCliente.png'
import { getAuth } from '../../services/authService'
import { fetchUsuarioDashboard } from '../../services/dashboardService'

const router = useRouter()
const auth = ref(getAuth())
const loading = ref(false)
const errorMessage = ref('')
const dashboard = ref(null)

const displayName = computed(() => dashboard.value?.nombre || auth.value?.nombre || 'Usuario')
const email = computed(() => dashboard.value?.email || auth.value?.email || '')
const initial = computed(() => displayName.value.trim().charAt(0).toUpperCase() || 'U')

async function loadDashboard() {
  loading.value = true
  errorMessage.value = ''

  try {
    dashboard.value = await fetchUsuarioDashboard()
  } catch (error) {
    errorMessage.value = error?.response?.data?.details?.[0] || error?.message || 'No se pudo cargar el panel de usuario.'
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadDashboard()
})

function goToReservations() {
  router.push({ name: 'dashboard-usuario-reservas' })
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  const d = new Date(dateStr + 'T00:00:00')
  return d.toLocaleDateString('es-ES', { day: 'numeric', month: 'short', year: 'numeric' })
}

function estadoBadgeClass(estado) {
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
    <header class="dashboard-header">
      <div>
        <p class="eyebrow mb-1">Área personal</p>
        <h1 class="h2 fw-bold mb-1">Hola, {{ displayName }}</h1>
        <p class="text-muted mb-0">Bienvenida a tu panel de control personal.</p>
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

      <div class="hero-panel shadow-sm mb-4">
        <div class="hero-panel__text">
          <h2 class="fw-bold mb-2">¿Lista para tu próxima experiencia local?</h2>
          <p class="text-muted mb-4">
            Encuentra y reserva servicios en los comercios de tu barrio de forma rápida y sencilla.
          </p>
          <button class="btn btn-primary px-4 py-2 fw-semibold" @click="goToReservations">
            <i class="bi bi-calendar-plus me-1"></i> Ver mis reservas actuales
          </button>
        </div>
        <div class="hero-panel__image">
          <img :src="callegestionCliente" alt="Ilustración de barrio" />
        </div>
      </div>

      <div class="row g-4 mb-4">
        <div class="col-md-4">
          <div class="metric-card shadow-sm" @click="goToReservations" style="cursor: pointer;">
            <p class="text-muted mb-2">Reservas activas</p>
            <div class="metric-value">{{ dashboard?.reservasActivas ?? 0 }}</div>
            <small class="text-success">+{{ dashboard?.reservasEstaSemana ?? 0 }} esta semana</small>
          </div>
        </div>
        <div class="col-md-4">
          <div class="metric-card shadow-sm">
            <p class="text-muted mb-2">Comercios favoritos</p>
            <div class="metric-value">{{ dashboard?.favoritosCount ?? 0 }}</div>
            <small class="text-primary">Tus lugares guardados</small>
          </div>
        </div>
        <div class="col-md-4">
          <div class="metric-card shadow-sm">
            <p class="text-muted mb-2">Estado de cuenta</p>
            <div class="metric-value">Activo</div>
            <small class="text-muted">Sesión iniciada correctamente</small>
          </div>
        </div>
      </div>

      <div class="row g-4">
        <div class="col-lg-8">
          <div class="panel-card shadow-sm h-100">
            <div class="panel-card__head">
              <h5 class="fw-bold mb-0">Últimas reservas</h5>
              <span class="text-muted small">Actividad reciente</span>
            </div>
            <div class="table-responsive">
              <table class="table align-middle mb-0">
                <thead>
                  <tr>
                    <th>Comercio</th>
                    <th>Fecha</th>
                    <th>Hora</th>
                    <th class="text-end">Estado</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="res in (dashboard?.ultimasReservas || [])" :key="res.id">
                    <td class="fw-semibold">{{ res.comercioNombre }}</td>
                    <td>{{ formatDate(res.fechaReserva) }}</td>
                    <td>{{ res.horaInicio?.slice(0,5) || '-' }}</td>
                    <td class="text-end">
                      <span class="status-badge" :class="estadoBadgeClass(res.estadoReserva)">{{ estadoLabel(res.estadoReserva) }}</span>
                    </td>
                  </tr>
                  <tr v-if="!dashboard?.ultimasReservas?.length">
                    <td colspan="4" class="text-center text-muted py-3">No tienes reservas recientes</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div class="col-lg-4">
          <div class="panel-card shadow-sm h-100">
            <div class="panel-card__head">
              <h5 class="fw-bold mb-0">Perfil</h5>
              <span class="text-muted small">Cuenta activa</span>
            </div>
            <div class="d-grid gap-3">
              <div>
                <div class="text-muted small mb-1">Nombre</div>
                <div class="fw-semibold">{{ displayName }}</div>
              </div>
              <div>
                <div class="text-muted small mb-1">Correo</div>
                <div class="fw-semibold">{{ email }}</div>
              </div>
              <RouterLink class="btn btn-outline-primary fw-semibold" :to="{ name: 'dashboard-usuario-perfil' }">
                Ver perfil
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
  color: var(--db-primary);
}

.hero-panel,
.panel-card,
.metric-card {
  background: #ffffff;
  border-radius: 1.25rem;
  border: 1px solid rgba(15, 23, 42, 0.08);
}

.hero-panel {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(280px, 0.9fr);
  overflow: hidden;
}

.hero-panel__text {
  padding: 2rem;
}

.hero-panel__image {
  min-height: 100%;
}

.hero-panel__image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.metric-card {
  padding: 1.25rem;
}

.metric-value {
  font-size: 2rem;
  font-weight: 800;
  color: #0f172a;
}

.panel-card {
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
  .hero-panel {
    grid-template-columns: 1fr;
  }
}
</style>