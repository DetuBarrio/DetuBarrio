<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';

const listaReservas = ref([]);
const isLoading = ref(true); // 🔄 Nuevo estado de carga inicializado en true

// Estado del filtro seleccionado ('TODAS', 'CONFIRMADA', 'CANCELADA', 'FINALIZADA')
const filtroActual = ref('TODAS');

onMounted(async () => {
  isLoading.value = true; // Aseguramos que se active al montar el componente
  try {
    const idComercio = localStorage.getItem('comercioId');
    if (idComercio) {
      const response = await axios.get(`http://localhost:8080/api/reservas/comercio/${idComercio}`);
      listaReservas.value = response.data;
      console.log("🔍 DATOS REALES DE LAS RESERVAS:", response.data);
    }
  } catch (error) {
    console.error("Error al cargar las reservas en la agenda:", error);
  } finally {
    isLoading.value = false; // 🏁 Finaliza la carga pase lo que pase
  }
});

// --- CONTADORES DINÁMICOS BASADOS EN EL ESTADO DINÁMICO ---
const totalReservas = computed(() => listaReservas.value.length);
const totalConfirmadas = computed(() => listaReservas.value.filter(r => estadoDinamico(r) === 'CONFIRMADA').length);
const totalCanceladas = computed(() => listaReservas.value.filter(r => estadoDinamico(r) === 'CANCELADA').length);
const totalFinalizadas = computed(() => listaReservas.value.filter(r => estadoDinamico(r) === 'FINALIZADA').length);

// Lista que se renderiza dinámicamente aplicando el filtro seleccionado
const reservasFiltradas = computed(() => {
  if (filtroActual.value === 'TODAS') return listaReservas.value;
  return listaReservas.value.filter(reserva => estadoDinamico(reserva) === filtroActual.value);
});

// --- DETECTORES INTELIGENTES PARA PASAR DE ID A DATOS REALES ---

function extraerFecha(reserva) {
  if (reserva.idDisponibilidad && typeof reserva.idDisponibilidad === 'object') return reserva.idDisponibilidad.fecha;
  if (reserva.disponibilidad && typeof reserva.disponibilidad === 'object') return reserva.disponibilidad.fecha;
  return reserva.fecha || '';
}

function extraerHoraInicio(reserva) {
  if (reserva.idDisponibilidad && typeof reserva.idDisponibilidad === 'object') return reserva.idDisponibilidad.horaInicio;
  if (reserva.disponibilidad && typeof reserva.disponibilidad === 'object') return reserva.disponibilidad.horaInicio;
  return reserva.horaInicio || '';
}

function extraerHoraFin(reserva) {
  if (reserva.idDisponibilidad && typeof reserva.idDisponibilidad === 'object') return reserva.idDisponibilidad.horaFin;
  if (reserva.disponibilidad && typeof reserva.disponibilidad === 'object') return reserva.disponibilidad.horaFin;
  return reserva.horaFin || '';
}

function extraerUsuarioNombre(reserva) {
  if (reserva.idUsuario && typeof reserva.idUsuario === 'object') return reserva.idUsuario.nombre;
  if (reserva.usuario && typeof reserva.usuario === 'object') return reserva.usuario.nombre;
  return reserva.nombreUsuario || reserva.usuarioNombre || reserva.clienteNombre || null;
}

function extraerUsuarioEmail(reserva) {
  if (reserva.idUsuario && typeof reserva.idUsuario === 'object') return reserva.idUsuario.email || reserva.idUsuario.correo;
  if (reserva.usuario && typeof reserva.usuario === 'object') return reserva.usuario.email || reserva.usuario.correo;
  return reserva.emailUsuario || reserva.usuarioEmail || reserva.clienteEmail || reserva.email || null;
}

function extraerUsuarioId(reserva) {
  if (reserva.idUsuario && typeof reserva.idUsuario === 'object') return reserva.idUsuario.id || reserva.idUsuario.idUsuario;
  if (reserva.usuario && typeof reserva.usuario === 'object') return reserva.usuario.id || reserva.usuario.idUsuario;
  return reserva.idUsuario || reserva.usuarioId || 'N/A';
}

// --- FORMATEADORES ---

function formatearFecha(reserva) {
  const fechaStr = extraerFecha(reserva);
  if (!fechaStr) return 'Sin fecha';
  try {
    const opciones = { weekday: 'long', day: 'numeric', month: 'long' };
    const fecha = new Date(fechaStr);
    if (isNaN(fecha.getTime())) return fechaStr; 
    const resultado = fecha.toLocaleDateString('es-ES', opciones);
    return resultado.charAt(0).toUpperCase() + resultado.slice(1);
  } catch (e) {
    return fechaStr;
  }
}

function formatHora(horaStr) {
  if (!horaStr) return '';
  return String(horaStr).substring(0, 5);
}

function estadoDinamico(reserva) {
  const estadoOriginal = reserva.estadoReserva || reserva.estado || 'CONFIRMADA';
  const fechaStr = extraerFecha(reserva);
  if (!fechaStr) return estadoOriginal;

  const ahora = new Date();
  const fechaReserva = new Date(fechaStr);
  const horaFinStr = extraerHoraFin(reserva);
  
  if (horaFinStr) {
    const [horas, mins] = horaFinStr.split(':');
    fechaReserva.setHours(horas, mins, 0);
  } else {
    fechaReserva.setHours(23, 59, 59); 
  }

  if (ahora > fechaReserva && estadoOriginal === 'CONFIRMADA') {
    return 'FINALIZADA';
  }
  return estadoOriginal;
}

// Colores de badges estilizados y más llamativos
function claseEstadoPremium(estado) {
  if (estado === 'FINALIZADA') return 'bg-primary-light text-primary border-primary-subtle';
  if (estado === 'CONFIRMADA') return 'bg-success-light text-success border-success-subtle';
  if (estado === 'CANCELADA') return 'bg-danger-light text-danger border-danger-subtle';
  return 'bg-secondary-light text-secondary border-secondary-subtle';
}

async function borrarReserva(reserva) {
  const idReserva = reserva.idReserva || reserva.id;
  if (!idReserva) {
    alert("No se pudo encontrar el ID de esta reserva.");
    return;
  }
  if (!confirm('¿Estás seguro de que deseas eliminar esta cita?')) return;
  
  try {
    await axios.delete(`http://localhost:8080/api/reservas/${idReserva}`);
    listaReservas.value = listaReservas.value.filter(r => (r.id !== idReserva && r.idReserva !== idReserva));
    alert("Reserva eliminada con éxito.");
  } catch (error) {
    console.error("Error al borrar la reserva:", error);
    alert("No se pudo borrar del servidor.");
  }
}
</script>

<template>
  <div class="agenda-container mt-2">
    <header class="view-header mb-4">
      <div>
        <p class="eyebrow mb-1">Gestión operativa</p>
        <h1 class="h3 fw-extrabold text-dark mb-1">Historial de Reservas</h1>
        <p class="text-muted small mb-0">Filtra, controla y haz seguimiento de las reservas registradas en tu establecimiento.</p>
      </div>
    </header>

    <div class="filter-wrapper p-2 bg-white rounded-4 shadow-sm mb-4 d-flex gap-2 flex-wrap">
      <button 
        class="btn filter-btn" 
        :class="{ active: filtroActual === 'TODAS' }"
        @click="filtroActual = 'TODAS'"
      >
        Todas <span class="badge-count">{{ totalReservas }}</span>
      </button>
      
      <button 
        class="btn filter-btn btn-filter-success" 
        :class="{ active: filtroActual === 'CONFIRMADA' }"
        @click="filtroActual = 'CONFIRMADA'"
      >
        <i class="bi bi-calendar-check me-1.5"></i> Confirmadas 
        <span class="badge-count">{{ totalConfirmadas }}</span>
      </button>
      
      <button 
        class="btn filter-btn btn-filter-danger" 
        :class="{ active: filtroActual === 'CANCELADA' }"
        @click="filtroActual = 'CANCELADA'"
      >
        <i class="bi bi-calendar-x me-1.5"></i> Canceladas 
        <span class="badge-count">{{ totalCanceladas }}</span>
      </button>
      
      <button 
        class="btn filter-btn btn-filter-primary" 
        :class="{ active: filtroActual === 'FINALIZADA' }"
        @click="filtroActual = 'FINALIZADA'"
      >
        <i class="bi bi-bookmark-star me-1.5"></i> Finalizadas 
        <span class="badge-count">{{ totalFinalizadas }}</span>
      </button>
    </div>

    <div v-if="isLoading" class="d-flex flex-column align-items-center justify-content-center py-5 my-4 bg-white rounded-4 shadow-sm border border-light">
      <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">
        <span class="visually-hidden">Cargando...</span>
      </div>
      <p class="text-muted mt-3 fw-medium mb-0">Buscando el historial de reservas...</p>
    </div>

    <div v-else class="card border-0 shadow-sm rounded-4 overflow-hidden">
      <div class="table-responsive">
        <table class="table custom-table mb-0 align-middle">
          <thead>
            <tr>
              <th scope="col" class="ps-4 py-3.5 fs-6_5 text-start">Fecha y Hora</th>
              <th scope="col" class="py-3.5 fs-6_5 text-center">Cliente</th>
              <th scope="col" class="py-3.5 fs-6_5 text-center">Correo Electrónico</th>
              <th scope="col" class="py-3.5 fs-6_5 text-center">Estado</th>
              <th scope="col" class="pe-4 py-3.5 fs-6_5 text-center">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="reservasFiltradas.length === 0">
              <td colspan="5" class="text-center py-5 text-muted">
                <i class="bi bi-calendar-minus display-5 d-block mb-3 text-placeholder"></i>
                <p class="mb-1 fw-bold fs-5 text-dark">No se encontraron reservas</p>
                <p class="small mb-0">No hay citas registradas que coincidan con el estado seleccionado.</p>
              </td>
            </tr>

            <tr v-for="reserva in reservasFiltradas" :key="reserva.id || reserva.idReserva" class="table-row-hover">
              
              <td class="ps-4 py-4">
                <div class="fw-bold text-dark fs-5 mb-1.5">{{ formatearFecha(reserva) }}</div>
                <div class="text-muted fw-semibold" v-if="extraerHoraInicio(reserva)">
                  <span class="badge-time bg-light text-dark px-3 py-1.5 rounded-3 border d-inline-flex align-items-center gap-2">
                    <i class="bi bi-clock-fill text-primary"></i>
                    {{ formatHora(extraerHoraInicio(reserva)) }} 
                    <span v-if="extraerHoraFin(reserva)"> - {{ formatHora(extraerHoraFin(reserva)) }}</span>
                  </span>
                </div>
              </td>

              <td class="text-center py-4 fs-5 fw-bold text-dark">
                {{ extraerUsuarioNombre(reserva) || ('ID: ' + extraerUsuarioId(reserva)) }}
              </td>

              <td class="text-center py-4">
                <span v-if="extraerUsuarioEmail(reserva)" class="text-secondary fs-5_5 font-monospace bg-light px-2.5 py-1 rounded">
                  {{ extraerUsuarioEmail(reserva) }}
                </span>
                <span v-else class="text-warning fs-6 fw-bold italic">No disponible</span>
              </td>

              <td class="text-center py-4">
                <span class="badge badge-premium rounded-pill px-4 py-2 fw-extrabold text-uppercase tracking-wider shadow-xs border" 
                      :class="claseEstadoPremium(estadoDinamico(reserva))">
                  {{ estadoDinamico(reserva) }}
                </span>
              </td>

              <td class="text-center pe-4 py-4">
                <button class="btn btn-action-delete-large rounded-3 px-3.5 py-2 fw-bold transition-all" @click="borrarReserva(reserva)">
                  <i class="bi bi-trash3-fill me-1.5"></i> Borrar
                </button>
              </td>

            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<style scoped>
.agenda-container {
  animation: fadeIn 0.22s ease-out;
}

.fw-extrabold { font-weight: 800; }
.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 0.75rem;
  color: #3a86ff;
  font-weight: 700;
}

/* Envoltura de Filtros */
.filter-wrapper {
  border: 1px solid #e2e8f0;
}

/* Botones de control de filtro */
.filter-btn {
  background: transparent;
  border: 1px solid transparent;
  color: #64748b;
  font-weight: 600;
  padding: 0.55rem 1.25rem;
  border-radius: 10px;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.2s ease;
}
.filter-btn:hover {
  background-color: #f1f5f9;
  color: #1e293b;
}

.badge-count {
  background-color: #e2e8f0;
  color: #475569;
  font-size: 0.75rem;
  font-weight: 700;
  padding: 0.15rem 0.5rem;
  border-radius: 6px;
}

/* Variaciones Activas del Selector de Filtros */
.filter-btn.active {
  background-color: #0f172a;
  color: #ffffff;
}
.filter-btn.active .badge-count {
  background-color: rgba(255, 255, 255, 0.2);
  color: #ffffff;
}
.btn-filter-success.active { background-color: #10b981; }
.btn-filter-danger.active { background-color: #ef4444; }
.btn-filter-primary.active { background-color: #3a86ff; }

/* Estructuración de Tabla Centrada y Corpulenta */
.custom-table thead th {
  background-color: #f8fafc;
  color: #475569;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  border-bottom: 2px solid #e2e8f0;
}

.fs-6_5 {
  font-size: 0.85rem;
}

.table-row-hover {
  transition: background-color 0.15s ease;
}
.table-row-hover:hover {
  background-color: #f8fafc;
}

/* Tipografías de tamaño intermedio aumentado */
.fs-5 {
  font-size: 1.05rem !important;
}
.fs-5_5 {
  font-size: 0.95rem;
}

/* Badge de Horas interno */
.badge-time {
  font-size: 0.88rem;
}

/* Píldora de estado Premium de mayor formato */
.badge-premium {
  font-size: 0.825rem;
  display: inline-block;
}

/* Botón Borrar Grande y Estilizado */
.btn-action-delete-large {
  background-color: #ffffff;
  color: #dc2626;
  border: 1px solid #fca5a5;
  font-size: 0.9rem;
  box-shadow: 0 1px 2px rgba(0,0,0,0.04);
}
.btn-action-delete-large:hover {
  background-color: #fef2f2;
  color: #b91c1c;
  border-color: #ef4444;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px rgba(220, 38, 38, 0.08);
}

/* Colores de Fondos Claros para Badges con bordes suaves */
.bg-primary-light { background-color: rgba(58, 134, 255, 0.1); }
.bg-success-light { background-color: rgba(16, 185, 129, 0.1); }
.bg-danger-light { background-color: rgba(239, 68, 68, 0.1); }
.bg-secondary-light { background-color: rgba(100, 116, 139, 0.1); }

.tracking-wider { letter-spacing: 0.06em; }
.text-placeholder { color: #cbd5e1; }
.shadow-xs { box-shadow: 0 2px 4px rgba(0,0,0,0.03); }
.transition-all { transition: all 0.2s ease; }

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(4px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>