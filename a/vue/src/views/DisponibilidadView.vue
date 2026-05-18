<template>
  <div class="gestion-citas-wrapper bg-light min-vh-100">
    <div class="container-fluid py-4">
      <div class="row g-4">
        
        <div class="col-lg-8">
          <div class="main-card shadow-sm border-0 rounded-4 bg-white p-4">
            <header class="mb-4 d-flex justify-content-between align-items-center">
              <div>
                <h2 class="fw-bold text-dark-blue mb-1">Gestión de Citas</h2>
                <p class="text-muted small">Configura fechas y horas específicas para tus clientes.</p>
              </div>
              <button class="btn btn-outline-primary rounded-pill px-4" @click="$router.push('/dashboard/comercio')">
                <i class="bi bi-arrow-left me-2"></i> Volver
              </button>
            </header>

            <section class="new-schedule-section mb-5 p-4 rounded-3 bg-aliceblue border border-info-subtle">
              <h5 class="fw-bold mb-3 text-primary">Añadir Nuevo Horario</h5>
              <div class="row g-3">
                <div class="col-md-4">
                  <label class="form-label fw-semibold small">Seleccionar Fecha</label>
                  <input type="date" v-model="nuevaFecha" class="form-control border-0 shadow-sm">
                </div>
                <div class="col-md-4">
                  <label class="form-label fw-semibold small">Hora Inicio</label>
                  <input type="time" v-model="horaInicio" class="form-control border-0 shadow-sm">
                </div>
                <div class="col-md-4">
                  <label class="form-label fw-semibold small">Hora Fin</label>
                  <input type="time" v-model="horaFin" class="form-control border-0 shadow-sm">
                </div>
              </div>
              <button class="btn btn-primary-detu w-100 mt-4 py-2 fw-bold shadow-sm" @click="agregarALista">
                <i class="bi bi-plus-circle me-2"></i> Añadir a la lista temporal
              </button>
            </section>

            <div class="table-responsive rounded-3 border">
              <table class="table table-hover align-middle mb-0">
                <thead class="bg-primary text-white">
                  <tr>
                    <th class="px-4 py-3">Fecha</th>
                    <th class="px-4 py-3">Franja Horaria</th>
                    <th class="px-4 py-3 text-center">Estado / Acción</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="h in listaHorarios" :key="'db-' + h.id">
                    <td class="px-4 py-3 fw-medium text-secondary">{{ h.fecha }}</td>
                    <td class="px-4 py-3">
                      <span class="badge bg-primary-subtle text-primary rounded-pill px-3 py-2">
                        <i class="bi bi-clock me-1"></i> {{ h.horaInicio }} - {{ h.horaFin }}
                      </span>
                    </td>
                    <td class="px-4 py-3 text-center">
                      <button class="btn btn-icon-danger" @click="eliminar(h.id, null, false)">
                        <i class="bi bi-trash3-fill"></i>
                      </button>
                    </td>
                  </tr>

                  <tr v-for="(n, index) in nuevosIntervalos" :key="'new-' + index" class="table-warning shadow-none">
                    <td class="px-4 py-3 fw-bold">{{ n.fecha }}</td>
                    <td class="px-4 py-3 fw-bold">{{ n.inicio }} - {{ n.fin }}</td>
                    <td class="px-4 py-3 text-center">
                      <div class="d-flex align-items-center justify-content-center gap-2">
                        <span class="badge bg-warning text-dark border border-warning-subtle">No guardado</span>
                        <button class="btn btn-icon-danger" @click="eliminar(null, index, true)">
                          <i class="bi bi-x-circle-fill"></i>
                        </button>
                      </div>
                    </td>
                  </tr>

                  <tr v-if="listaHorarios.length === 0 && nuevosIntervalos.length === 0">
                    <td colspan="3" class="text-center py-5 text-muted">
                      <i class="bi bi-calendar-x d-block fs-2 mb-2"></i>
                      No hay horarios configurados.
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <button 
              class="btn btn-success-detu w-100 mt-4 py-3 fw-bold shadow" 
              @click="guardarCambios"
              :disabled="nuevosIntervalos.length === 0"
            >
              <i class="bi bi-cloud-upload me-2"></i> 
              {{ nuevosIntervalos.length > 0 ? `Guardar ${nuevosIntervalos.length} cambios en el servidor` : 'Sincronizado con la nube' }}
            </button>
          </div>
        </div>

        <div class="col-lg-4">
          <div class="calendar-card shadow-sm border-0 rounded-4 bg-white p-4 h-100">
            <h5 class="fw-bold text-dark-blue mb-4"><i class="bi bi-calendar3 me-2"></i> Vista Mensual</h5>
            
            <div class="placeholder-calendar rounded-4 d-flex align-items-center justify-content-center mb-4">
               <div class="text-center">
                 <i class="bi bi-calendar2-check display-4 text-primary opacity-25"></i>
                 <p class="mt-2 text-muted px-3 small">Próximamente: Calendario interactivo con tus citas.</p>
               </div>
            </div>

            <div class="selected-day-details p-3 border-start border-primary border-4 bg-light rounded-end">
              <h6 class="fw-bold text-primary mb-2">Resumen de Actividad</h6>
              <ul class="list-unstyled mb-0 small">
                <li class="mb-2"><i class="bi bi-check2-circle text-success me-2"></i> <strong>{{ listaHorarios.length }}</strong> horarios activos</li>
                <li v-if="nuevosIntervalos.length > 0"><i class="bi bi-exclamation-triangle text-warning me-2"></i> <strong>{{ nuevosIntervalos.length }}</strong> pendientes de guardar</li>
              </ul>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'DisponibilidadView',
  data() {
    return {
      comercioId: null, // Cambiado de 1 a null
      nuevaFecha: '',
      horaInicio: '',
      horaFin: '',
      listaHorarios: [],
      nuevosIntervalos: [],
      selectedDateLabel: 'Hoy'
    }
  },
  async mounted() {
    // 1. Obtener el usuarioId del localStorage
    const usuarioId = localStorage.getItem('usuarioId');
    
    if (usuarioId) {
      try {
        // 2. Buscar a qué comercio pertenece este usuario
        const resComercio = await axios.get(`http://localhost:8080/api/comercios/usuario/${usuarioId}`);
        this.comercioId = resComercio.data.id;
        
        // 3. Una vez tenemos el ID real, cargamos sus horarios
        this.cargarHorarios();
      } catch (error) {
        console.error("Error al identificar el comercio del usuario:", error);
      }
    } else {
      console.warn("No se encontró usuarioId en el almacenamiento local.");
    }
  },
  methods: {
    async cargarHorarios() {
      try {
        const response = await axios.get(`http://localhost:8080/api/disponibilidad/comercio/${this.comercioId}`);
        this.listaHorarios = response.data;
      } catch (error) {
        console.error("Error al cargar:", error);
      }
    },
    agregarALista() {
      if (this.nuevaFecha && this.horaInicio && this.horaFin) {
        this.nuevosIntervalos.push({
          fecha: this.nuevaFecha,
          inicio: this.horaInicio + ":00",
          fin: this.horaFin + ":00"
        });
        this.horaInicio = '';
        this.horaFin = '';
      }
    },
    async guardarCambios() {
    try {
        const payload = {
        comercioId: this.comercioId,
        intervalos: this.nuevosIntervalos
        };
        await axios.post('http://localhost:8080/api/disponibilidad/configurar', payload);
        this.nuevosIntervalos = [];
        this.cargarHorarios();
        alert("¡Guardado con éxito!");
    } catch (error) {
        // Si el backend lanza el error de solapamiento, lo mostramos aquí
        const mensaje = error.response?.data?.error || "Error al sincronizar";
        alert("Cuidado: " + mensaje);
    }
    },
    async eliminar(id, index, esNuevo) {
      if (confirm("¿Seguro que quieres borrar este horario?")) {
        if (esNuevo) {
          this.nuevosIntervalos.splice(index, 1);
        } else {
          try {
            await axios.delete(`http://localhost:8080/api/disponibilidad/${id}`);
            this.cargarHorarios();
          } catch (error) {
            alert("No se pudo borrar de la base de datos.");
          }
        }
      }
    }
  }
}
</script>

<style scoped>
.text-dark-blue { color: #1a237e; }
.bg-aliceblue { background-color: #f8fbff; }
.btn-primary-detu { background-color: #3b82f6; color: white; border: none; border-radius: 10px; transition: all 0.3s; }
.btn-primary-detu:hover { background-color: #2563eb; transform: translateY(-1px); }
.btn-success-detu { background-color: #1e293b; color: white; border: none; border-radius: 10px; }
.btn-success-detu:disabled { background-color: #94a3b8; cursor: not-allowed; }
.placeholder-calendar { background: #f8fafc; border: 2px dashed #cbd5e1; min-height: 250px; }
.btn-icon-danger { background: transparent; color: #dc3545; border: none; font-size: 1.1rem; padding: 0 10px; }
.btn-icon-danger:hover { color: #a71d2a; }
.bg-primary { background-color: #3b82f6 !important; }
.table-warning { background-color: #fff9db !important; }
</style>