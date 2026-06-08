<template>
  <div class="reserva-section card shadow-sm p-4 mt-4">
    <h3 class="h5 fw-bold mb-3">📅 Reserva tu cita</h3>
    
    <div v-if="estaLogueado">
      <div class="mb-3">
        <label class="form-label small text-muted">Selecciona un día:</label>
        <input type="date" class="form-control" v-model="fechaSeleccionada" :min="today">
      </div>

      <div v-if="horasFiltradas.length > 0" class="mb-4">
        <label class="form-label small text-muted">Horas disponibles:</label>
        <div class="d-flex flex-wrap gap-2">
          <button 
            v-for="hueco in horasFiltradas" 
            :key="hueco.id" 
            @click="seleccionarHueco(hueco)"
            :class="['btn btn-outline-primary btn-sm', selectedId === hueco.id ? 'active' : '']"
          >
            {{ formatHora(hueco.horaInicio) }} - {{ formatHora(hueco.horaFin) }}
          </button>
        </div>
      </div>
      <p v-else-if="fechaSeleccionada" class="text-danger small">No hay citas para este día.</p>

      <button 
        @click="confirmarReserva" 
        :disabled="!selectedId" 
        class="btn btn-primary w-100 mt-2"
      >
        Confirmar Reserva
      </button>
    </div>

    <div v-else class="text-center py-3">
      <div class="fs-2 mb-2">🔒</div>
      <h4 class="h6 fw-bold text-dark mb-2">Acceso Restringido</h4>
      <p class="text-muted small mb-3">
        Para reservar citas en los comercios de tu barrio tienes que logearte en tu cuenta.
      </p>
      <a href="#/login" class="btn btn-primary btn-sm w-100 fw-bold">
        🔐 Iniciar Sesión / Registrarse
      </a>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import { getAuth } from '../services/authService'; 
import { apiUrl } from '../config/api';
import { showToast } from '../utils/toastService';

const props = defineProps(['idComercio', 'disponibilidades']);
const fechaSeleccionada = ref('');
const selectedId = ref(null);
const today = new Date().toISOString().split('T')[0];

// Por defecto asumimos que NO está logueado hasta validar credenciales reales
const estaLogueado = ref(false);

onMounted(() => {
  // 🎯 Validación estricta cruzando authService y localStorage
  const authData = getAuth();
  const token = authData?.token || localStorage.getItem('token');
  const usuarioLogueadoId = authData?.id || authData?.usuarioId || localStorage.getItem('usuarioId');
  
  // Si tiene un ID de usuario pero NO tiene token de sesión, consideramos que la sesión caducó o es falsa
  if (usuarioLogueadoId && token) {
    estaLogueado.value = true;
  } else {
    estaLogueado.value = false;
    // Limpieza preventiva por si acaso quedaron residuos antiguos de ID en el navegador
    localStorage.removeItem('usuarioId'); 
  }
});

// Filtra las horas según la fecha elegida
const horasFiltradas = computed(() => {
  if (!fechaSeleccionada.value || !props.disponibilidades) return [];
  
  const fechaBusqueda = fechaSeleccionada.value.trim();

  return props.disponibilidades.filter(d => {
    return d.fecha === fechaBusqueda && !d.reservado;
  });
});

const seleccionarHueco = (hueco) => {
  selectedId.value = hueco.id;
};

const formatHora = (hora) => {
  if (!hora) return '';
  return hora.substring(0, 5); 
};

const confirmarReserva = async () => {
  if (!estaLogueado.value) {
    showToast('Para reservar citas tienes que iniciar sesión.', 'warning');
    return;
  }

  try {
    const authData = getAuth();
    const token = authData?.token || localStorage.getItem('token');
    const usuarioLogueadoId = authData?.id || authData?.usuarioId || localStorage.getItem('usuarioId');

    const reservaData = {
      idComercio: props.idComercio,
      idUsuario: parseInt(usuarioLogueadoId, 10), 
      idDisponibilidad: selectedId.value,    
      idServicio: null 
    };
    
    const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};
    
    await axios.post(apiUrl('/api/reservas'), reservaData, config);
    
    showToast('¡Reserva realizada con éxito!', 'success');
    location.reload(); 
    
  } catch (error) {
    console.error("Error al reservar:", error.response?.data || error);
    showToast('Error al reservar: ' + (error.response?.data?.message || "Error interno del servidor"), 'error');
  }
};
</script>