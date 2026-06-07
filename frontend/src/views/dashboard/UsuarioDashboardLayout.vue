<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import AppBreadcrumbs from '../../components/AppBreadcrumbs.vue'
import { clearAuth, getAuth } from '../../services/authService'

const router = useRouter()
const route = useRoute()
const auth = ref(getAuth())

function syncAuth() {
  auth.value = getAuth()
}

const displayName = computed(() => auth.value?.nombre || 'Usuario')
const email = computed(() => auth.value?.email || '')
const initial = computed(() => displayName.value.trim().charAt(0).toUpperCase() || 'U')

onMounted(() => {
  window.addEventListener('detubarrio-auth-changed', syncAuth)
})

onUnmounted(() => {
  window.removeEventListener('detubarrio-auth-changed', syncAuth)
})

function handleLogout() {
  clearAuth()
  router.push({ name: 'login' })
}

function isActiveRoute(name) {
  return route.name === name
}
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
        <RouterLink class="sidebar-link" :class="{ active: isActiveRoute('dashboard-usuario') }" :to="{ name: 'dashboard-usuario' }">
          <i class="bi bi-grid me-2"></i> Panel general
        </RouterLink>
        <RouterLink class="sidebar-link" :class="{ active: isActiveRoute('dashboard-usuario-reservas') }" :to="{ name: 'dashboard-usuario-reservas' }">
          <i class="bi bi-calendar-check me-2"></i> Mis reservas
        </RouterLink>
        <RouterLink class="sidebar-link" :class="{ active: isActiveRoute('dashboard-usuario-perfil') }" :to="{ name: 'dashboard-usuario-perfil' }">
          <i class="bi bi-person me-2"></i> Mi perfil
        </RouterLink>
        <RouterLink class="sidebar-link" :class="{ active: isActiveRoute('dashboard-usuario-ajustes') }" :to="{ name: 'dashboard-usuario-ajustes' }">
          <i class="bi bi-gear me-2"></i> Ajustes
        </RouterLink>
      </nav>

      <div class="sidebar-actions">
        <RouterLink class="btn btn-light w-100 fw-semibold" to="/comercios">
          <i class="bi bi-shop me-1"></i> Explorar comercios
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
</style>