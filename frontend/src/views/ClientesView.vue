<template>
  <div class="gestion-clientes-wrapper bg-light min-vh-100">
    <div class="container-fluid py-4">
      <div class="main-card shadow-sm border-0 rounded-4 bg-white p-4">
        
        <header class="mb-4 d-flex justify-content-between align-items-center flex-wrap gap-3">
          <div>
            <h2 class="fw-bold text-dark-blue mb-1">Mis Clientes</h2>
            <p class="text-muted small">Listado de clientes que han realizado reservas en tu comercio.</p>
          </div>
          
          <div class="d-flex gap-2 bg-light p-1 rounded-pill border">
            <button 
              v-for="f in opcionesFiltro" 
              :key="f.value"
              :class="['btn rounded-pill px-3 btn-sm fw-bold transition-all', filtroSeleccionado === f.value ? 'btn-primary-detu shadow-sm' : 'btn-clean text-secondary']"
              @click="cambiarFiltro(f.value)"
              :disabled="cargando"
            >
              <i :class="[f.icon, 'me-1']"></i> {{ f.label }}
            </button>
          </div>
        </header>

        <div class="table-responsive rounded-3 border tabla-scrollable">
          <table class="table table-hover align-middle mb-0">
            <thead class="bg-primary text-white">
              <tr>
                <th class="px-4 py-3">Nombre Completo</th>
                <th class="px-4 py-3">Email de Contacto</th>
                <th class="px-4 py-3 text-center">Última Reserva Realizada</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="cargando">
                <td colspan="3" class="text-center py-5">
                  <div class="spinner-border text-primary my-2" role="status">
                    <span class="visually-hidden">Cargando...</span>
                  </div>
                  <div class="text-muted small">Cargando clientes...</div>
                </td>
              </tr>

              <template v-else>
                <tr v-for="cliente in listaClientes" :key="cliente.id">
                  <td class="px-4 py-3 fw-bold text-dark">
                    <div class="d-flex align-items-center gap-2">
                      <i class="bi bi-person-circle fs-5 text-primary opacity-75"></i>
                      {{ cliente.nombre }}
                    </div>
                  </td>
                  <td class="px-4 py-3 text-secondary">{{ cliente.email }}</td>
                  <td class="px-4 py-3 text-center">
                    <span class="badge bg-primary-subtle text-primary rounded-pill px-3 py-2 fw-bold">
                      <i class="bi bi-calendar-event me-1"></i> {{ formatFecha(cliente.ultimaReserva) }}
                    </span>
                  </td>
                </tr>

                <tr v-if="listaClientes.length === 0">
                  <td colspan="3" class="text-center py-5 text-muted">
                    <i class="bi bi-people-fill d-block display-5 mb-2 text-secondary opacity-25"></i>
                    Ningún cliente coincide con el filtro seleccionado para este periodo.
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { apiUrl } from '../config/api';

export default {
  name: 'ClientesView',
  data() {
    return {
      comercioId: null,
      filtroSeleccionado: 'todos',
      listaClientes: [],
      cargando: false // 🌟 Estado inicial de carga en falso
    }
  },
  async mounted() {
    const usuarioId = localStorage.getItem('usuarioId');
    if (usuarioId) {
      try {
        const resComercio = await axios.get(apiUrl(`/api/comercios/usuario/${usuarioId}`));
        this.comercioId = resComercio.data.id;
        this.cargarClientes();
      } catch (error) {
        console.error("Error al vincular el comercio:", error);
      }
    }
  },
  methods: {
    async cargarClientes() {
      this.cargando = true; // 🌟 Activar ruedecilla antes de la petición
      try {
        const response = await axios.get(apiUrl(`/api/clientes/comercio/${this.comercioId}`), {
          params: { filtro: this.filtroSeleccionado }
        });
        this.listaClientes = response.data;
      } catch (error) {
        console.error("Error al obtener la lista de clientes:", error);
      } finally {
        this.cargando = false; // 🌟 Desactivar ruedecilla al terminar
      }
    },
    cambiarFiltro(nuevoFiltro) {
      this.filtroSeleccionado = nuevoFiltro;
      this.cargarClientes();
    },
    formatFecha(fechaRaw) {
      if (!fechaRaw) return '-';
      const f = new Date(fechaRaw);
      return f.toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' });
    }
  }
}
</script>

<style scoped>
.text-dark-blue { color: #1a237e; }
.bg-primary { background-color: #3b82f6 !important; }

.btn-primary-detu { 
  background-color: #3b82f6; 
  color: white; 
  border: none; 
}

.btn-clean {
  background: transparent;
  border: none;
}

.transition-all {
  transition: all 0.2s ease-in-out;
}

/* 📦 CUADRADO DESLIZABLE CON SOPORTE STICKY HEADER */
.tabla-scrollable {
  max-height: 500px;
  overflow-y: auto;
  position: relative;
}

.tabla-scrollable thead th {
  position: sticky;
  top: 0;
  z-index: 5;
  background-color: #3b82f6 !important;
  box-shadow: inset 0 -1px 0 rgba(0,0,0,0.15);
}
</style>