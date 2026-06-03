<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRouter, useRoute } from 'vue-router'
import AppBreadcrumbs from '../../components/AppBreadcrumbs.vue'
import { clearAuth, getAuth } from '../../services/authService'

const router = useRouter()
const route = useRoute()
const auth = ref(getAuth())

const displayName = computed(() => auth.value?.nombre || 'Comercio')
const email = computed(() => auth.value?.email || '')
const initial = computed(() => displayName.value.trim().charAt(0).toUpperCase() || 'C')

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

function handleLogout() {
  clearAuth()
  router.push({ name: 'login' })
}

onMounted(() => {
  redirectByRole()
})
</script>

<template>
  <main class="dashboard-shell">
    <aside class="dashboard-sidebar">
      <div class="profile-card">
        <div class="profile-avatar">{{ initial }}</div>
        <div class="overflow-hidden">
          <h6 class="mb-1 fw-bold text-dark text-truncate">{{ displayName }}</h6>
          <small class="text-muted text-truncate d-block">{{ email }}</small>
        </div>
      </div>

      <nav class="sidebar-nav">
        <RouterLink class="sidebar-link" :class="{ active: route.path === '/dashboard/comercio' }" to="/dashboard/comercio">
          <i class="bi bi-grid me-2"></i> Panel general
        </RouterLink>
        <RouterLink class="sidebar-link" :class="{ active: route.path.startsWith('/dashboard/comercio/reservas') }" to="/dashboard/comercio/reservas">
          <i class="bi bi-calendar-check me-2"></i> Reservas
        </RouterLink>
        <RouterLink class="sidebar-link" :class="{ active: route.path.startsWith('/dashboard/comercio/disponibilidad') }" to="/dashboard/comercio/disponibilidad">
          <i class="bi bi-clock me-2"></i> Horarios
        </RouterLink>
        <RouterLink class="sidebar-link" :class="{ active: route.path.startsWith('/dashboard/comercio/clientes') }" to="/dashboard/comercio/clientes">
          <i class="bi bi-people me-2"></i> Clientes
        </RouterLink>
        <RouterLink class="sidebar-link" :class="{ active: route.path.startsWith('/dashboard/comercio/configuracion') }" to="/dashboard/comercio/configuracion">
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
      <AppBreadcrumbs />
      <RouterView />
    </section>
  </main>
</template>

<style scoped>
.dashboard-shell {
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  min-height: calc(100vh - 72px);
  background: #f6f9fe;
}

.dashboard-sidebar {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  padding: 1.5rem;
  background: #ffffff;
  border-right: 1px solid rgba(15, 23, 42, 0.08);
}

.profile-card {
  display: flex;
  align-items: center;
  gap: 0.9rem;
  padding: 1rem;
  border-radius: 1rem;
  background: #f8fbff;
}

.profile-avatar {
  width: 44px;
  height: 44px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--db-primary, #025197);
  color: white;
  font-weight: 700;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.sidebar-link {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  text-decoration: none;
  color: #64748b;
  padding: 0.8rem 1rem;
  border-radius: 0.95rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.sidebar-link:hover,
.sidebar-link.active {
  background: #eaf2ff;
  color: var(--db-primary, #025197);
  font-weight: 700;
}

.sidebar-actions {
  margin-top: auto;
  display: grid;
  gap: 0.75rem;
}

.dashboard-content {
  padding: 1.5rem;
}

/* ====== Estilos del contenido (hero, métricas, etc.) ====== */
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
}
.icon-round:hover {
  background-color: #f8fafc;
}

.avatar-badge {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--db-primary, #025197);
  color: white;
  font-weight: 700;
}

.hero-panel {
  background: #ffffff;
  border: 1px solid #e2e8f0 !important;
}
.btn-hero-action {
  background: linear-gradient(135deg, #3a86ff 0%, #2563eb 100%);
  border: none;
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

.metric-card-custom { background: #ffffff; }
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
.metric-value-custom { font-size: 2.3rem; }

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

.tracking-wider { letter-spacing: 0.1em; }
.leading-none { line-height: 1; }
.leading-tight { line-height: 1.25; }
.leading-relaxed { line-height: 1.625; }
.fs-7 { font-size: 0.85rem; }
.shadow-xs { box-shadow: 0 1px 2px rgba(0,0,0,0.03); }
.transition-all { transition: all 0.2s ease; }
</style>