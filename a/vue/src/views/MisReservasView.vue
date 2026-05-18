<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const listaReservas = ref([]);

onMounted(async () => {
  try {
    const idComercio = localStorage.getItem('comercioId');
    if (idComercio) {
      const response = await axios.get(`http://localhost:8080/api/reservas/comercio/${idComercio}`);
      listaReservas.value = response.data;
      console.log("🔍 DATOS REALES DE LAS RESERVAS:", response.data);
    }
  } catch (error) {
    console.error("Error al cargar las reservas en la agenda:", error);
  }
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

// Busca el nombre del cliente (en objeto anidado o propiedad plana)
function extraerUsuarioNombre(reserva) {
  if (reserva.idUsuario && typeof reserva.idUsuario === 'object') return reserva.idUsuario.nombre;
  if (reserva.usuario && typeof reserva.usuario === 'object') return reserva.usuario.nombre;
  return reserva.nombreUsuario || reserva.usuarioNombre || reserva.clienteNombre || null;
}

// Busca el correo del cliente para la nueva columna
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

function claseEstado(estado) {
  if (estado === 'FINALIZADA') return 'badge bg-secondary';
  if (estado === 'CONFIRMADA') return 'badge bg-success';
  if (estado === 'CANCELADA') return 'badge bg-danger';
  return 'badge bg-primary';
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
  <div class="agenda-container mt-4">
    <div class="table-responsive shadow-sm rounded">
      <table class="table table-hover align-middle bg-white mb-0">
        <thead class="table-dark">
          <tr>
            <th class="py-3 px-4">Fecha y Hora</th>
            <th class="py-3 px-4">Cliente</th>
            <th class="py-3 px-4">Correo Electrónico</th>
            <th class="py-3 px-4">Estado</th>
            <th class="py-3 px-4">Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="listaReservas.length === 0">
            <td colspan="5" class="text-center py-5 text-muted">
              Aún no tienes citas reservadas.
            </td>
          </tr>

          <tr v-for="reserva in listaReservas" :key="reserva.id || reserva.idReserva">
            
            <td class="px-4">
              <div class="fw-bold text-primary mb-1">{{ formatearFecha(reserva) }}</div>
              <div class="text-muted small fw-medium" v-if="extraerHoraInicio(reserva)">
                <span class="bg-light px-2 py-1 rounded border">
                  {{ formatHora(extraerHoraInicio(reserva)) }} 
                  <span v-if="extraerHoraFin(reserva)"> - {{ formatHora(extraerHoraFin(reserva)) }}</span>
                </span>
              </div>
            </td>

            <td class="px-4">
              <div class="fw-bold text-dark">
                {{ extraerUsuarioNombre(reserva) || ('Usuario ID: ' + extraerUsuarioId(reserva)) }}
              </div>
            </td>

            <td class="px-4">
              <span v-if="extraerUsuarioEmail(reserva)" class="text-muted text-break">
                {{ extraerUsuarioEmail(reserva) }}
              </span>
              <span v-else class="text-warning small italic">No disponible</span>
            </td>

            <td class="px-4">
              <span class="px-3 py-2 rounded-pill fw-bold text-uppercase" style="font-size: 0.75rem;" :class="claseEstado(estadoDinamico(reserva))">
                {{ estadoDinamico(reserva) }}
              </span>
            </td>

            <td class="px-4">
              <button class="btn btn-outline-danger btn-sm fw-bold px-3" @click="borrarReserva(reserva)">
                Borrar
              </button>
            </td>

          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.agenda-container {
  max-width: 1300px;
  margin: 0 auto;
}
.table-dark th {
  background-color: #11284b;
  border-bottom: 0;
  font-weight: 600;
  letter-spacing: 0.5px;
}
</style>