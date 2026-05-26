<template>
  <div class="card border-0 shadow-sm p-4 sticky-lg-top" style="top: 2rem; background-color: #ffffff; border-radius: 12px;">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h5 class="fw-bold m-0" style="color: #0f172a;">Filtros</h5>
      <button @click="$emit('clear')" class="btn btn-sm btn-outline-primary rounded-pill px-3">
        Limpiar Filtros
      </button>
    </div>

    <div class="mb-4">
      <label class="form-label small fw-bold text-muted text-uppercase mb-2">Categoría</label>
      <select :value="categoriaSeleccionada" 
              @change="$emit('update:categoria-seleccionada', $event.target.value)" 
              class="form-select">
        <option value="">Todas las categorías</option>
        <option v-for="cat in categorias" :key="cat.id" :value="cat.id">
          {{ cat.nombreCategoria || cat.nombre }}
        </option>
      </select>
    </div>

    <div class="mb-4">
      <label class="form-label small fw-bold text-muted text-uppercase mb-2">Valoración</label>
      <select :value="valoracionSeleccionada" 
              @change="$emit('update:valoracion-seleccionada', $event.target.value)" 
              class="form-select">
        <option v-for="opcion in valoracionOpciones" :key="opcion.value" :value="opcion.value">
          {{ opcion.label }}
        </option>
      </select>
    </div>

    <div class="mb-3">
      <label class="form-label small fw-bold text-muted text-uppercase mb-2">Horario</label>
      <select :value="horarioSeleccionado" 
              @change="$emit('update:horario-seleccionado', $event.target.value)" 
              class="form-select">
        <option v-for="opcion in horarioOpciones" :key="opcion.value" :value="opcion.value">
          {{ opcion.label }}
        </option>
      </select>
    </div>
  </div>
</template>

<script setup>
defineProps({
  categorias: Array,
  categoriaSeleccionada: [String, Number],
  valoracionSeleccionada: [String, Number],
  horarioSeleccionado: String,
  valoracionOpciones: Array,
  horarioOpciones: Array
})

defineEmits([
  'update:categoria-seleccionada', 
  'update:valoracion-seleccionada', 
  'update:horario-seleccionado',
  'clear'
])
</script>