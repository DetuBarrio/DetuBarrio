<template>
  <div style="background-color: #f8fafc; min-height: 100vh; padding: 2rem 1rem; font-family: sans-serif;">
    <div style="max-width: 1000px; margin: 0 auto; display: flex; flex-direction: column; gap: 1.5rem;">
      
      <div style="background-color: #ffffff; border-radius: 16px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); border: 1px solid #e2e8f0; overflow: hidden;">
        <header style="background: linear-gradient(135deg, #1e1b4b 0%, #0f172a 100%); padding: 2.5rem 2rem; color: #ffffff;">
          <h1 style="color: #025197 !important; margin: 0; font-size: 2rem; font-weight: 800; letter-spacing: -0.025em; text-shadow: 0 1px 2px rgba(0,0,0,0.2);">
            Mis Reservas
          </h1>
          <p style="color: #cbd5e1 !important; margin: 0.5rem 0 0 0; font-size: 0.9rem; font-weight: 400;">
            Visualiza y gestiona las citas pendientes y completadas en tus comercios de confianza.
          </p>
        </header>

        <div style="display: flex; background-color: #f1f5f9; border-bottom: 1px solid #e2e8f0; padding: 0 1rem;">
          <button @click="pestanaActiva = 'proximas'" 
                  :style="obtenerEstiloPestana(pestanaActiva === 'proximas')">
            📅 Próximas Citas
          </button>
          <button @click="pestanaActiva = 'historial'" 
                  :style="obtenerEstiloPestana(pestanaActiva === 'historial')">
            📜 Historial de Visitas
          </button>
        </div>

        <div style="padding: 2rem; background-color: #ffffff;">
          
          <div v-if="loading" style="text-align: center; padding: 3rem; color: #64748b; font-weight: 600;">
            ⏳ Buscando tus reservas en el sistema...
          </div>

          <div v-else-if="filtrarReservas.length === 0" style="text-align: center; padding: 4rem 2rem; border: 2px dashed #e2e8f0; border-radius: 12px; color: #64748b;">
            <p style="font-size: 1.2rem; margin: 0 0 0.5rem 0; font-weight: 700;">No tienes citas registradas aquí</p>
            <p style="font-size: 0.875rem; margin: 0;">Cuando reserves en algún bar o tienda de tu barrio, aparecerán en esta lista.</p>
          </div>

          <div v-else style="display: flex; flex-direction: column; gap: 1.25rem;">
            <div v-for="reserva in filtrarReservas" :key="reserva.id" 
                 style="background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 1.5rem; box-shadow: 0 2px 4px rgba(0,0,0,0.02); display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
              
              <div style="display: flex; flex-direction: column; gap: 0.5rem;">
                <h3 style="margin: 0; color: #025197; font-size: 1.25rem; font-weight: 800;">
                  {{ reserva.nombreComercio || 'Establecimiento Local' }}
                </h3>
                
                <div style="display: flex; gap: 1.5rem; color: #475569; font-size: 0.9rem; font-weight: 500; flex-wrap: wrap;">
                  <span><span>📆</span> <strong>Fecha:</strong> {{ formatearFecha(reserva.disponibilidad?.fecha) }}</span>
                  
                  <span>
                    <span>⏰</span> <strong>Franja Horaria:</strong> 
                    {{ obtenerRangoHorario(reserva.disponibilidad) }}
                  </span>
                </div>
              </div>

              <div style="display: flex; align-items: center; gap: 1rem;">
                <span :style="obtenerEstiloEstado(reserva.estadoReserva)">
                  {{ reserva.estadoReserva || 'CONFIRMADA' }}
                </span>

                <button v-if="pestanaActiva === 'proximas' && reserva.estadoReserva !== 'CANCELADA'"
                        @click="cancelarCita(reserva.id)"
                        style="background-color: #ef4444; color: #ffffff; border: none; padding: 0.65rem 1.25rem; border-radius: 8px; font-weight: 700; font-size: 0.8rem; cursor: pointer; transition: background-color 0.2s; text-transform: uppercase; letter-spacing: 0.025em; box-shadow: 0 2px 4px rgba(239, 68, 68, 0.2);">
                  ❌ Cancelar Cita
                </button>
              </div>

            </div>
          </div>

        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import { getAuth } from '../services/authService';
import { apiUrl } from '../config/api';

const pestanaActiva = ref('proximas');
const loading = ref(false);
const reservas = ref([]);

onMounted(async () => {
  await cargarReservasDelUsuario();
});

async function cargarReservasDelUsuario() {
  const authData = getAuth();
  const rawId = authData?.id || authData?.usuarioId || localStorage.getItem('usuarioId');
  const usuarioId = rawId ? parseInt(rawId, 10) : null; 
  const token = authData?.token;

  if (!usuarioId || isNaN(usuarioId)) {
    console.warn("No se localizó un ID de usuario válido numérico.");
    return;
  }

  loading.value = true;
  try {
    const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};
    const response = await axios.get(apiUrl(`/api/reservas/usuario/${usuarioId}`), config);
    reservas.value = response.data;
  } catch (error) {
    console.error("Error recuperando las reservas de la base de datos:", error);
  } finally {
    loading.value = false;
  }
}

const filtrarReservas = computed(() => {
  const fechaHoy = new Date().toISOString().split('T')[0];

  return reservas.value.filter(reserva => {
    const estado = (reserva.estadoReserva || 'CONFIRMADA').toUpperCase();
    const fechaCita = reserva.disponibilidad?.fecha || '';

    if (pestanaActiva.value === 'proximas') {
      return fechaCita >= fechaHoy && estado !== 'CANCELADA';
    } else {
      return fechaCita < fechaHoy || estado === 'CANCELADA';
    }
  });
});

async function cancelarCita(idReserva) {
  if (!confirm("¿Estás seguro de que deseas anular esta cita de manera permanente?")) return;

  const authData = getAuth();
  const token = authData?.token;
  const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};

  try {
    await axios.put(apiUrl(`/api/reservas/${idReserva}/cancelar`), {}, config);
    alert("Cita anulada correctamente.");
    await cargarReservasDelUsuario();
  } catch (error) {
    console.error("Fallo al cancelar la cita en el servidor:", error);
    alert("No se pudo procesar la cancelación en este momento.");
  }
}

// --- MAQUETACIÓN Y ESTILOS DINÁMICOS ---

function obtenerEstiloPestana(activa) {
  return {
    padding: '1.2rem 1.75rem',
    fontSize: '0.9rem',
    fontWeight: '700',
    backgroundColor: activa ? '#ffffff' : 'transparent',
    color: activa ? '#025197' : '#64748b',
    border: 'none',
    borderBottom: activa ? '3px solid #025197' : '3px solid transparent',
    cursor: 'pointer',
    transition: 'all 0.15s ease-in-out',
    outline: 'none'
  };
}

function obtenerEstiloEstado(estado) {
  const estilosBase = {
    padding: '0.4rem 0.85rem',
    borderRadius: '50px',
    fontSize: '0.75rem',
    fontWeight: '800',
    textTransform: 'uppercase',
    letterSpacing: '0.05em'
  };

  const estadoNormalizado = (estado || 'CONFIRMADA').toUpperCase();

  switch (estadoNormalizado) {
    case 'CONFIRMADA':
    case 'ACEPTADA':
      return { ...estilosBase, backgroundColor: '#dcfce7', color: '#15803d' };
    case 'PENDIENTE':
      return { ...estilosBase, backgroundColor: '#fef9c3', color: '#a16207' };
    case 'CANCELADA':
      return { ...estilosBase, backgroundColor: '#fee2e2', color: '#b91c1c' };
    default:
      return { ...estilosBase, backgroundColor: '#f1f5f9', color: '#475569' };
  }
}

function formatearFecha(fechaInversa) {
  if (!fechaInversa) return '';
  const fragmentos = fechaInversa.split('-');
  if (fragmentos.length !== 3) return fechaInversa;
  return `${fragmentos[2]}/${fragmentos[1]}/${fragmentos[0]}`;
}

function formatearHora(horaRaw) {
  if (!horaRaw) return null;
  const partes = horaRaw.split(':');
  if (partes.length >= 2) {
    return `${partes[0]}:${partes[1]}`;
  }
  return horaRaw;
}

function obtenerRangoHorario(disp) {
  if (!disp) return 'Por definir';
  
  const inicioRaw = disp.hora || disp.horaInicio || disp.inicio || disp.franjaHoraria;
  const finRaw = disp.horaFin || disp.fin || disp.horaFinal;

  const inicioFormateado = formatearHora(inicioRaw);
  const finFormateado = formatearHora(finRaw);

  if (inicioFormateado && finFormateado) {
    return `${inicioFormateado} - ${finFormateado}`;
  } else if (inicioFormateado) {
    return inicioFormateado;
  }
  
  return 'Por definir';
}
</script>