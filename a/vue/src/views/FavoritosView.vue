<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { getAuth } from '../services/authService'
import axios from 'axios' // 🌟 Importamos Axios
import comercioDefault from '../assets/images/buenaMesa.png'

const favoritos = ref([])
const API_URL = 'http://localhost:8080'

const comercioImageModules = import.meta.glob('../assets/images/*.{png,jpg,jpeg,webp,svg,gif}', {
  eager: true,
  import: 'default',
})
const comercioImagesByName = Object.fromEntries(
  Object.entries(comercioImageModules).map(([path, url]) => [path.split('/').pop(), url]),
)

function getImagenComercio(imageUrl) {
  if (!imageUrl) return comercioDefault
  if (typeof imageUrl === 'string' && imageUrl.startsWith('/uploads')) return `${API_URL}${imageUrl}`
  if (/^https?:\/\//i.test(imageUrl) || imageUrl.startsWith('data:')) return imageUrl

  const fileName = imageUrl.split('/').pop()
  if (comercioImagesByName[fileName]) return comercioImagesByName[fileName]
  return comercioDefault
}

// --- NUEVA LÓGICA DE CARGA DESDE EL BACKEND ---
async function cargarFavoritos() {
  try {
    const authData = getAuth()
    const token = authData?.token || localStorage.getItem('token')
    if (!token) return

    const response = await axios.get(`${API_URL}/api/favoritos`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    
    favoritos.value = response.data
  } catch (error) {
    console.error("Error al cargar favoritos de la base de datos:", error)
  }
}

async function eliminarDeFavoritos(id) {
  try {
    const authData = getAuth()
    const token = authData?.token || localStorage.getItem('token')
    if (!token) return

    // Como es un conmutador (toggle) y ya sabemos que está en favoritos, esto lo eliminará
    await axios.post(`${API_URL}/api/favoritos/${id}`, {}, {
      headers: { Authorization: `Bearer ${token}` }
    })
    
    // Lo eliminamos visualmente del array para no tener que volver a hacer una petición GET
    favoritos.value = favoritos.value.filter(item => Number(item.id) !== Number(id))
  } catch (error) {
    console.error("Error al eliminar favorito:", error)
  }
}

onMounted(() => {
  cargarFavoritos()
})
</script>

<template>
  <div class="favoritos-page py-5 bg-light min-vh-100">
    <div class="container">
      <div class="d-flex align-items-center justify-content-between mb-5">
        <div>
          <h1 class="fw-bold text-dark mb-1">Mis Comercios Favoritos</h1>
          <p class="text-muted mb-0">Accede rápidamente a los negocios locales que más te gustan</p>
        </div>
        <RouterLink to="/comercios" class="btn btn-outline-primary btn-sm rounded-pill px-4 fw-semibold">
          <i class="bi bi-search me-1"></i> Explorar más
        </RouterLink>
      </div>

      <div v-if="favoritos.length === 0" class="card border-0 shadow-sm rounded-4 text-center p-5 bg-white">
        <div class="p-4 mx-auto bg-light rounded-circle text-danger mb-4 d-flex align-items-center justify-content-center" style="width: 80px; height: 80px;">
          <i class="bi bi-heartbreak fs-1"></i>
        </div>
        <h3 class="fw-bold text-dark">Aún no tienes favoritos</h3>
        <p class="text-muted mx-auto mb-4" style="max-width: 450px;">
          Explora los comercios de tu comunidad y marca con un corazón aquellos que quieras guardar para verlos aquí en cualquier momento.
        </p>
        <RouterLink to="/comercios" class="btn btn-primary rounded-3 px-4 fw-bold shadow-sm">
          Ver Comercios del Barrio
        </RouterLink>
      </div>

      <div v-else class="row g-4">
        <div class="col-md-6 col-lg-4 col-xl-3" v-for="comercio in favoritos" :key="comercio.id">
          <div class="card h-100 border-0 shadow-sm rounded-4 overflow-hidden position-relative card-hover">
            
            <button 
              @click="eliminarDeFavoritos(comercio.id)"
              class="btn btn-remove-fav position-absolute top-0 end-0 m-3 rounded-circle d-flex align-items-center justify-content-center shadow-sm"
              title="Quitar de favoritos"
            >
              <i class="bi bi-heart-fill text-danger"></i>
            </button>

            <RouterLink :to="{ name: 'comercio-detalle', params: { id: comercio.id } }" class="text-decoration-none text-dark h-100 d-flex flex-column">
              <img
                :src="getImagenComercio(comercio.logo)"
                class="card-img-top object-fit-cover"
                :alt="comercio.nombreComercio"
                style="height: 160px;"
              />
              <div class="card-body d-flex flex-column flex-grow-1">
                <span class="badge bg-primary-subtle text-primary border border-primary-subtle rounded-pill px-2.5 py-1 small fw-bold alignment-badge mb-2">
                  {{ comercio.categoria }}
                </span>
                <h5 class="fw-bold mb-1 text-truncate">{{ comercio.nombreComercio }}</h5>
                <p class="text-muted small text-truncate-2 flex-grow-1 mb-3">{{ comercio.descripcion || 'Sin descripción.' }}</p>
                
                <div class="text-warning small mt-auto pt-2 d-flex align-items-center gap-1 border-top">
                  <template v-for="n in 5" :key="n">
                    <i class="bi" :class="n <= Math.round(comercio.puntuacionMedia || 0) ? 'bi-star-fill' : 'bi-star text-muted'"></i>
                  </template>
                  <span class="text-muted ms-1 fw-medium">
                    {{ Number(comercio.puntuacionMedia || 0).toFixed(1) }} ({{ comercio.totalResenas || 0 }})
                  </span>
                </div>
              </div>
            </RouterLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.alignment-badge {
  width: fit-content;
}
.text-truncate-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.card-hover {
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}
.card-hover:hover {
  transform: translateY(-4px);
  box-shadow: 0 0.75rem 1.5rem rgba(0, 0, 0, 0.08) !important;
}
.btn-remove-fav {
  background-color: rgba(255, 255, 255, 0.9);
  border: none;
  width: 36px;
  height: 36px;
  z-index: 3;
  transition: transform 0.2s ease;
}
.btn-remove-fav:hover {
  transform: scale(1.15);
  background-color: #ffffff;
}
</style>