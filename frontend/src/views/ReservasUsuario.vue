<template>
  <div class="reservas-page">
    <div class="page-header">
      <p class="eyebrow mb-1">Área personal</p>
      <h1 class="h3 fw-bold mb-1">Mis reservas</h1>
      <p class="text-muted mb-0">Visualiza y gestiona tus citas pendientes y completadas.</p>
    </div>

    <div class="reservas-card">
      <div class="tabs-header">
        <button
          class="tab-btn"
          :class="{ active: pestanaActiva === 'proximas' }"
          @click="pestanaActiva = 'proximas'"
        >
          <i class="bi bi-calendar-check me-1"></i> Próximas
        </button>
        <button
          class="tab-btn"
          :class="{ active: pestanaActiva === 'historial' }"
          @click="pestanaActiva = 'historial'"
        >
          <i class="bi bi-clock-history me-1"></i> Historial
        </button>
      </div>

      <div class="tabs-content">
        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
          <p class="mt-3 text-muted mb-0 fw-medium">Cargando tus reservas...</p>
        </div>

        <div v-else-if="filtrarReservas.length === 0" class="empty-state">
          <i class="bi bi-calendar-x fs-1 text-muted"></i>
          <p class="fw-bold mb-1 mt-2">No tienes citas aquí</p>
          <p class="text-muted small mb-0">Cuando reserves en algún comercio, aparecerán en esta lista.</p>
        </div>

        <div v-else class="reservas-list">
          <div v-for="reserva in filtrarReservas" :key="reserva.id" class="reserva-item">
            <div class="reserva-info">
              <h5 class="fw-bold mb-1">{{ reserva.nombreComercio || 'Establecimiento' }}</h5>
              <div class="reserva-meta">
                <span><i class="bi bi-calendar3 me-1"></i> {{ formatearFecha(reserva.disponibilidad?.fecha) }}</span>
                <span><i class="bi bi-clock me-1"></i> {{ obtenerRangoHorario(reserva.disponibilidad) }}</span>
              </div>
            </div>
            <div class="reserva-actions">
              <span class="status-badge" :class="estadoClase(reserva.estadoReserva)">{{ estadoLabel(reserva.estadoReserva) }}</span>
              <button
                v-if="pestanaActiva === 'proximas' && reserva.estadoReserva !== 'CANCELADA'"
                class="btn btn-outline-danger btn-sm fw-bold rounded-pill"
                @click="cancelarCita(reserva.id)"
              >
                <i class="bi bi-x-lg me-1"></i> Cancelar
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { getAuth } from '../services/authService'
import { apiUrl } from '../config/api'
import { showToast } from '../utils/toastService'
import { showConfirm } from '../utils/confirmService'

const pestanaActiva = ref('proximas')
const loading = ref(false)
const reservas = ref([])

onMounted(async () => {
  await cargarReservasDelUsuario()
})

async function cargarReservasDelUsuario() {
  const authData = getAuth()
  const rawId = authData?.id || authData?.usuarioId || localStorage.getItem('usuarioId')
  const usuarioId = rawId ? parseInt(rawId, 10) : null
  const token = authData?.token

  if (!usuarioId || isNaN(usuarioId)) {
    console.warn('No se localizó un ID de usuario válido numérico.')
    return
  }

  loading.value = true
  try {
    const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {}
    const response = await axios.get(apiUrl(`/api/reservas/usuario/${usuarioId}`), config)
    reservas.value = response.data
  } catch (error) {
    console.error('Error recuperando las reservas:', error)
  } finally {
    loading.value = false
  }
}

const filtrarReservas = computed(() => {
  const fechaHoy = new Date().toISOString().split('T')[0]
  return reservas.value.filter(reserva => {
    const estado = (reserva.estadoReserva || 'CONFIRMADA').toUpperCase()
    const fechaCita = reserva.disponibilidad?.fecha || ''
    if (pestanaActiva.value === 'proximas') {
      return fechaCita >= fechaHoy && estado !== 'CANCELADA'
    } else {
      return fechaCita < fechaHoy || estado === 'CANCELADA'
    }
  })
})

async function cancelarCita(idReserva) {
  const confirmed = await showConfirm({ title: 'Cancelar reserva', message: '¿Estás seguro de que deseas cancelar esta reserva?' }); if (!confirmed) return
  const authData = getAuth()
  const token = authData?.token
  const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {}
  try {
    await axios.put(apiUrl(`/api/reservas/${idReserva}/cancelar`), {}, config)
    await cargarReservasDelUsuario()
    showToast('Reserva cancelada con éxito.', 'success')
  } catch (error) {
    console.error('Fallo al cancelar la cita:', error)
    showToast('No se pudo procesar la cancelación.', 'error')
  }
}

function formatearFecha(fechaInversa) {
  if (!fechaInversa) return '-'
  const partes = fechaInversa.split('-')
  if (partes.length !== 3) return fechaInversa
  return `${partes[2]}/${partes[1]}/${partes[0]}`
}

function formatearHora(horaRaw) {
  if (!horaRaw) return null
  const partes = horaRaw.split(':')
  return partes.length >= 2 ? `${partes[0]}:${partes[1]}` : horaRaw
}

function obtenerRangoHorario(disp) {
  if (!disp) return 'Por definir'
  const inicio = formatearHora(disp.hora || disp.horaInicio || disp.inicio)
  const fin = formatearHora(disp.horaFin || disp.fin || disp.horaFinal)
  if (inicio && fin) return `${inicio} - ${fin}`
  if (inicio) return inicio
  return 'Por definir'
}

function estadoClase(estado) {
  switch ((estado || 'CONFIRMADA').toUpperCase()) {
    case 'CONFIRMADA': return 'status-confirmada'
    case 'CANCELADA': return 'status-cancelada'
    default: return 'status-pendiente'
  }
}

function estadoLabel(estado) {
  switch ((estado || 'CONFIRMADA').toUpperCase()) {
    case 'CONFIRMADA': return 'Confirmada'
    case 'CANCELADA': return 'Cancelada'
    default: return 'Pendiente'
  }
}
</script>

<style scoped>
.reservas-page {
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

.reservas-card {
  background: #ffffff;
  border-radius: 1.25rem;
  border: 1px solid rgba(15, 23, 42, 0.08);
  overflow: hidden;
}

.tabs-header {
  display: flex;
  background: #f8fafc;
  border-bottom: 1px solid rgba(15, 23, 42, 0.06);
  padding: 0 1rem;
}

.tab-btn {
  padding: 1rem 1.5rem;
  font-size: 0.9rem;
  font-weight: 700;
  background: transparent;
  color: #64748b;
  border: none;
  border-bottom: 3px solid transparent;
  cursor: pointer;
  transition: all 0.15s ease;
}

.tab-btn:hover {
  color: #025197;
}

.tab-btn.active {
  color: #025197;
  border-bottom-color: #025197;
  background: #ffffff;
}

.tabs-content {
  padding: 1.5rem;
}

.empty-state {
  text-align: center;
  padding: 3rem 1rem;
  border: 2px dashed #e2e8f0;
  border-radius: 12px;
  color: #64748b;
}

.reservas-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.reserva-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  padding: 1.25rem;
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 1rem;
  transition: box-shadow 0.2s;
}

.reserva-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.reserva-info h5 {
  margin: 0;
  color: #0f172a;
}

.reserva-meta {
  display: flex;
  gap: 1.5rem;
  margin-top: 0.4rem;
  font-size: 0.85rem;
  color: #64748b;
  flex-wrap: wrap;
}

.reserva-actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-shrink: 0;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.35rem 0.8rem;
  border-radius: 999px;
  font-size: 0.75rem;
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

.btn-outline-danger {
  border-radius: 999px;
}
</style>
