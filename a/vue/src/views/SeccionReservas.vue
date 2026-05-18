<template>
  <div class="reserva-section card shadow-sm p-4 mt-4">
    <h3 class="h5 fw-bold mb-3">📅 Reserva tu cita</h3>
    
    <!-- DEBUG TEMPORAL: Borra esta línea cuando funcione -->
    <!-- <pre>{{ disponibilidades }}</pre> -->

    <!-- Paso 1: Selección de Fecha -->
    <div class="mb-3">
      <label class="form-label small text-muted">Selecciona un día:</label>
      <input type="date" class="form-control" v-model="fechaSeleccionada" :min="today">
    </div>

    <!-- Paso 2: Selección de Horas Disponibles -->
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

    <!-- Paso 3: Botón de Confirmar -->
    <button 
      @click="confirmarReserva" 
      :disabled="!selectedId" 
      class="btn btn-primary w-100 mt-2"
    >
      Confirmar Reserva
    </button>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import axios from 'axios';

const props = defineProps(['idComercio', 'disponibilidades']);
const fechaSeleccionada = ref('');
const selectedId = ref(null);
// Corregido: simplificado el manejo de la fecha de hoy
const today = new Date().toISOString().split('T')[0];

// Filtra las horas según la fecha elegida
const horasFiltradas = computed(() => {
  if (!fechaSeleccionada.value || !props.disponibilidades) return [];
  
  // Normalizamos la fecha seleccionada para asegurar que no hay espacios raros
  const fechaBusqueda = fechaSeleccionada.value.trim();

  return props.disponibilidades.filter(d => {
    // Si en la base de datos es '2026-05-15' y el input da '2026-05-15', esto entrará
    return d.fecha === fechaBusqueda && !d.reservado;
  });
});

const seleccionarHueco = (hueco) => {
  // CAMBIO: Usamos .id porque lo cambiamos en el Backend
  selectedId.value = hueco.id;
};

const formatHora = (hora) => {
  if (!hora) return '';
  return hora.substring(0, 5); 
};

const confirmarReserva = async () => {
  try {
    // 🎯 Clave exacta que vimos en tu consola de almacenamiento local
    const usuarioLogueadoId = localStorage.getItem('usuarioId');

    if (!usuarioLogueadoId) {
      alert('Debes iniciar sesión para poder reservar una cita.');
      return;
    }

    const reservaData = {
      idComercio: props.idComercio,
      idUsuario: parseInt(usuarioLogueadoId), 
      idDisponibilidad: selectedId.value,    
      idServicio: null 
    };
    
    await axios.post('http://localhost:8080/api/reservas', reservaData);
    
    alert('¡Reserva realizada con éxito!');
    location.reload(); 
    
  } catch (error) {
    console.error("Error al reservar:", error.response?.data || error);
    alert('Error al reservar: ' + (error.response?.data?.message || "Error interno del servidor"));
  }
};
</script>