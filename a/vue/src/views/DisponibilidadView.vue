<template>
  <div class="gestion-citas-wrapper bg-light min-vh-100">
    <div class="container-fluid py-4">
      <div class="row g-4">
        
        <div class="col-lg-8">
          <div class="main-card shadow-sm border-0 rounded-4 bg-white p-4">
            <header class="mb-4 d-flex justify-content-between align-items-center">
              <div>
                <h2 class="fw-bold text-dark-blue mb-1">Gestión de Citas</h2>
                <p class="text-muted small">Configura rangos de fechas y horas para generar tus citas automáticamente.</p>
              </div>
              <button class="btn btn-outline-primary rounded-pill px-4" @click="$router.push('/dashboard/comercio')">
                <i class="bi bi-arrow-left me-2"></i> Volver
              </button>
            </header>

            <section class="new-schedule-section mb-5 p-4 rounded-3 bg-aliceblue border border-info-subtle">
              <h5 class="fw-bold mb-3 text-primary"><i class="bi bi-magic me-2"></i>Generador Masivo de Disponibilidad</h5>
              
              <div class="row g-3">
                <div class="col-md-6 col-lg-3">
                  <label class="form-label fw-semibold small">Desde el día</label>
                  <input type="date" v-model="fechaInicio" class="form-control border-0 shadow-sm">
                </div>
                <div class="col-md-6 col-lg-3">
                  <label class="form-label fw-semibold small">Hasta el día</label>
                  <input type="date" v-model="fechaFin" class="form-control border-0 shadow-sm">
                </div>

                <div class="col-md-6 col-lg-3">
                  <label class="form-label fw-semibold small">Hora Apertura (Inicio)</label>
                  <input type="time" v-model="horaInicio" class="form-control border-0 shadow-sm">
                </div>
                <div class="col-md-6 col-lg-3">
                  <label class="form-label fw-semibold small">Hora Cierre (Fin)</label>
                  <input type="time" v-model="horaFin" class="form-control border-0 shadow-sm">
                </div>

                <div class="col-12">
                  <label class="form-label fw-semibold small">Duración de cada cita / intervalo</label>
                  <select v-model="duracionIntervalo" class="form-select border-0 shadow-sm">
                    <option :value="15">Cada 15 minutes</option>
                    <option :value="20">Cada 20 minutos</option>
                    <option :value="30">Cada 30 minutos</option>
                    <option :value="45">Cada 45 minutos</option>
                    <option :value="60">Cada 1 hora (60 min)</option>
                    <option :value="120">Cada 2 horas (120 min)</option>
                  </select>
                </div>
              </div>

              <button class="btn btn-primary-detu w-100 mt-4 py-25 fw-bold shadow-sm" @click="generarIntervalosLote">
                <i class="bi bi-cpu-fill me-2"></i> Procesar y Generar Bloques de Citas
              </button>
            </section>

            <div class="table-responsive rounded-3 border tabla-scrollable">
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
                    <td class="px-4 py-3 fw-bold">
                      <span class="badge bg-warning-subtle text-warning-dark rounded-pill px-3 py-2">
                        {{ n.inicio.substring(0,5) }} - {{ n.fin.substring(0,5) }}
                      </span>
                    </td>
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
                      No hay horarios configurados ni autogenerados en cola.
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
              {{ nuevosIntervalos.length > 0 ? `Subir y Activar ${nuevosIntervalos.length} Citas al Servidor` : 'Sincronizado con la nube' }}
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
      comercioId: null,
      fechaInicio: '',
      fechaFin: '',
      horaInicio: '',
      horaFin: '',
      duracionIntervalo: 30,
      listaHorarios: [],
      nuevosIntervalos: [],
      selectedDateLabel: 'Hoy'
    }
  },
  async mounted() {
    const usuarioId = localStorage.getItem('usuarioId');
    if (usuarioId) {
      try {
        const resComercio = await axios.get(`http://localhost:8080/api/comercios/usuario/${usuarioId}`);
        this.comercioId = resComercio.data.id;
        this.cargarHorarios();
      } catch (error) {
        console.error("Error al identificar el comercio del usuario:", error);
      }
    } else {
      console.warn("No se encontró usuarioId del almacenamiento local.");
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
    generarIntervalosLote() {
      if (!this.fechaInicio || !this.fechaFin || !this.horaInicio || !this.horaFin) {
        alert("Por favor, rellena los rangos completos (Fechas y Horas) para calcular los bloques.");
        return;
      }

      const inicioD = new Date(this.fechaInicio + 'T00:00:00');
      const finD = new Date(this.fechaFin + 'T00:00:00');

      if (inicioD > finD) {
        alert("La fecha de inicio no puede superar la fecha de finalización.");
        return;
      }

      if (this.horaInicio >= this.horaFin) {
        alert("La hora de apertura tiene que ser menor que la hora de cierre.");
        return;
      }

      let totalContador = 0;
      let diaActual = new Date(inicioD);

      while (diaActual <= finD) {
        const yyyy = diaActual.getFullYear();
        const mm = String(diaActual.getMonth() + 1).padStart(2, '0');
        const dd = String(diaActual.getDate()).padStart(2, '0');
        const fechaStr = `${yyyy}-${mm}-${dd}`;

        const [hIni, mIni] = this.horaInicio.split(':').map(Number);
        const [hFin, mFin] = this.horaFin.split(':').map(Number);

        let minutosActuales = hIni * 60 + mIni;
        const minutosLimite = hFin * 60 + mFin;
        const paso = Number(this.duracionIntervalo);

        while (minutosActuales + paso <= minutosLimite) {
          const iniH = Math.floor(minutosActuales / 60);
          const iniM = minutosActuales % 60;
          
          const finH = Math.floor((minutosActuales + paso) / 60);
          const finM = (minutosActuales + paso) % 60;

          const strInicio = String(iniH).padStart(2, '0') + ':' + String(iniM).padStart(2, '0') + ':00';
          const strFin = String(finH).padStart(2, '0') + ':' + String(finM).padStart(2, '0') + ':00';

          this.nuevosIntervalos.push({
            fecha: fechaStr,
            inicio: strInicio,
            fin: strFin
          });

          totalContador++;
          minutosActuales += paso;
        }
        diaActual.setDate(diaActual.getDate() + 1);
      }
      alert(`¡Éxito! Se han pre-calculado ${totalContador} bloques de citas en la tabla inferior.`);
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
        alert("¡Todos los bloques horarios se han subido con éxito!");
      } catch (error) {
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
/* 🕶️ CONTROL DE SCROLL Y CONTENEDOR TIPO CUADRADO */
.tabla-scrollable {
  max-height: 420px;       /* Ajusta esta altura al gusto para que parezca un cuadrado perfecto */
  overflow-y: auto;        /* Añade el scroll vertical automático */
  position: relative;
}

/* 📌 EFECTO STICKY: Mantiene los títulos arriba al bajar con el scroll */
.tabla-scrollable thead th {
  position: sticky;
  top: 0;
  z-index: 5;
  background-color: #3b82f6 !important; /* Mismo color azul primario para que tape los datos que suben */
  box-shadow: inset 0 -1px 0 rgba(0,0,0,0.12); /* Sutil línea separadora inferior */
}

/* Estilos complementarios estándar */
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
.text-warning-dark { color: #856404; }
.py-25 { padding-top: 0.65rem; padding-bottom: 0.65rem; }
</style>