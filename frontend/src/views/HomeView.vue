<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import fondo from '../assets/images/fondo.png';
import comercioDefault from '../assets/images/buenaMesa.png'; 
import { apiUrl } from '../config/api'

const router = useRouter()
const searchTerm = ref('')
const comerciosDestacados = ref([])

// Detecta automáticamente todas las imágenes procesadas por Vite en assets
const comercioImageModules = import.meta.glob('../assets/images/*.{png,jpg,jpeg,webp,svg,gif}', {
  eager: true,
  import: 'default',
})

const comercioImagesByName = Object.fromEntries(
  Object.entries(comercioImageModules).map(([path, url]) => [path.split('/').pop(), url]),
)

// Función inteligente idéntica a la de Comercios y Detalle
function getImagenComercio(imageUrl) {
  if (!imageUrl) {
    return comercioDefault // Si es null, usamos la imagen por defecto importada
  }

  if (typeof imageUrl === 'string' && imageUrl.startsWith('/uploads')) {
    return apiUrl(imageUrl)
  }

  if (/^https?:\/\//i.test(imageUrl) || imageUrl.startsWith('data:')) {
    return imageUrl
  }

  const fileName = imageUrl.split('/').pop()
  if (comercioImagesByName[fileName]) {
    return comercioImagesByName[fileName]
  }

  const cleanedImage = imageUrl.replace(/^\.\//, '').replace(/^images\//, '')
  const cleanedName = cleanedImage.split('/').pop()

  if (comercioImagesByName[cleanedName]) {
    return comercioImagesByName[cleanedName]
  }

  return imageUrl.startsWith('/') ? imageUrl : `/images/${cleanedName}`
}

function buscarComercios() {
  const query = searchTerm.value.trim()
  router.push({
    path: '/comercios',
    query: query ? { q: query } : {},
  })
}

onMounted(async () => {
  try {
    const response = await fetch(apiUrl('/api/comercios'))
    if (response.ok) {
      const data = await response.json()
      
      // Ordenamos por valoración media (de mayor a menor)
      comerciosDestacados.value = data
        .sort((a, b) => (b.media || b.puntuacionMedia || 0) - (a.media || a.puntuacionMedia || 0))
        .slice(0, 4) // Cogemos solo los 4 mejores
    }
  } catch (error) {
    console.error("Error al cargar los comercios destacados:", error)
  }
})
</script>

<template>
    <section class="py-3">
      <div class="container">
        <div
          class="position-relative rounded-5 overflow-hidden text-center text-white"
          style="min-height: 500px"
        >
          <img
            :src="fondo"
            alt=""
            aria-hidden="true"
            role="presentation"
            class="w-100 h-100 position-absolute top-0 start-0 object-fit-cover"
            style="filter: brightness(0.6); z-index: 1"
          />

          <div
            class="position-relative d-flex flex-column justify-content-center align-items-center h-100 p-4 p-lg-5"
            style="z-index: 2; height: 500px"
          >
            <h1 class="display-4 fw-bolder mb-3 text-white">
              Conecta con tu barrio.<br />
              Encuentra todo lo que <br />
              necesitas, al lado de casa.
            </h1>

            <p class="lead mb-4 fw-normal text-white">
              Apoya el comercio local y descubre los mejores productos y
              servicios cerca de ti.
            </p>

            <div
              class="bg-white p-2 rounded-4 shadow-lg w-100"
              style="max-width: 700px"
            >
              <form class="d-flex w-100" role="search" @submit.prevent="buscarComercios">
                <span class="input-group-text bg-white border-0 ps-3">
                  <i class="bi bi-search text-secondary"></i>
                </span>
                <label for="home-search" class="visually-hidden">Buscar comercios o servicios en tu barrio</label>
                <input
                  id="home-search"
                  class="form-control border-0 shadow-none ps-2"
                  type="search"
                  placeholder="Busca comercios o servicios en tu barrio"
                  aria-label="Buscar comercios o servicios en tu barrio"
                  v-model="searchTerm"
                />
                <button
                  class="btn btn-primary px-4 rounded-3 fw-bold"
                  type="submit"
                >
                  Buscar
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="py-5 bg-white">
      <div class="container text-center py-4">
        <h2 class="fw-bold mb-2">¿Qué es DetuBarrio?</h2>
        <p class="text-muted mb-5">
          Somos la plataforma que digitaliza y une a los negocios de tu barrio
          con sus vecinos.
        </p>

        <div class="row g-4">
          <div class="col-md-4">
            <div class="card h-100 border-0 shadow-sm py-4 rounded-4">
              <div class="card-body">
                <div class="bg-light d-inline-block p-3 rounded-circle mb-3 text-primary">
                  <i class="bi bi-heart-fill fs-3"></i>
                </div>
                <h5 class="fw-bold">Apoyo Local</h5>
                <p class="text-muted small px-3">
                  Cada servicio que haces ayuda a un emprendedor de tu comunidad.
                </p>
              </div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="card h-100 border-0 shadow-sm py-4 rounded-4">
              <div class="card-body">
                <div class="bg-light d-inline-block p-3 rounded-circle mb-3 text-primary">
                  <i class="bi bi-map-fill fs-3"></i>
                </div>
                <h5 class="fw-bold">Comodidad</h5>
                <p class="text-muted small px-3">
                  Encuentra lo que necesitas a solo unos pasos de tu casa.
                </p>
              </div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="card h-100 border-0 shadow-sm py-4 rounded-4">
              <div class="card-body">
                <div class="bg-light d-inline-block p-3 rounded-circle mb-3 text-primary">
                  <i class="bi bi-bag-fill fs-3"></i>
                </div>
                <h5 class="fw-bold">Variedad</h5>
                <p class="text-muted small px-3">
                  Descubre una gran oferta de productos y servicios uniques.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="py-5">
      <div class="container text-center">
        <h3 class="fw-bold mb-5">Explora por Categoría</h3>
        <div class="d-flex flex-wrap justify-content-center gap-5">
          
          <button
            @click="router.push({ path: '/comercios', query: { categoria: 'Salud' } })"
            class="btn btn-white shadow-sm rounded-4 px-5 py-4 d-flex align-items-center gap-3 fw-bold text-dark border fs-5"
          >
            <i class="bi bi-heart-pulse-fill text-primary"></i> Salud
          </button>

          <button
            @click="router.push({ path: '/comercios', query: { categoria: 'Estilismo' } })"
            class="btn btn-white shadow-sm rounded-4 px-5 py-4 d-flex align-items-center gap-3 fw-bold text-dark border fs-5"
          >
            <i class="bi bi-scissors text-primary"></i> Estilismo
          </button>

          <button
            @click="router.push({ path: '/comercios', query: { categoria: 'Otros' } })"
            class="btn btn-white shadow-sm rounded-4 px-5 py-4 d-flex align-items-center gap-3 fw-bold text-dark border fs-5"
          >
            <i class="bi bi-three-dots text-primary"></i> Otros
          </button>

        </div>
      </div>
    </section>

    <section class="py-5 bg-light">
      <div class="container">
        <h3 class="fw-bold text-center mb-5">Nuestros Comercios Destacados</h3>
        
        <div v-if="comerciosDestacados.length === 0" class="text-center text-muted">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Cargando...</span>
          </div>
        </div>

        <div v-else class="row g-4">
          <div class="col-md-6 col-lg-3" v-for="comercio in comerciosDestacados" :key="comercio.id">
            <RouterLink 
              :to="{ name: 'comercio-detalle', params: { id: comercio.id } }" 
              class="text-decoration-none text-dark"
            >
              <div class="card h-100 border-0 shadow-sm rounded-4 overflow-hidden card-hover">
                <img
                  :src="getImagenComercio(comercio.logo)"
                  class="card-img-top"
                  :alt="comercio.nombreComercio"
                  style="height: 150px; object-fit: cover"
                />
                <div class="card-body">
                  <h6 class="fw-bold mb-0 text-truncate">{{ comercio.nombreComercio }}</h6>
                  <small class="text-muted">{{ comercio.categoria }}</small>
                  
                  <div class="text-warning small mt-2 d-flex align-items-center gap-1">
                    <template v-for="n in 5" :key="n">
                      <i class="bi" :class="n <= Math.round(comercio.media || comercio.puntuacionMedia || 0) ? 'bi-star-fill' : 'bi-star text-muted'"></i>
                    </template>
                    <span class="text-muted ms-1">
                      {{ (comercio.media || comercio.puntuacionMedia) ? Number(comercio.media || comercio.puntuacionMedia).toFixed(1) : '0.0' }} ({{ comercio.total || comercio.totalResenas || 0 }})
                    </span>
                  </div>
                </div>
              </div>
            </RouterLink>
          </div>
        </div>
      </div>
    </section>

    <section class="py-5">
      <div class="container">
        <div class="row g-4">
          <div class="col-md-6">
            <div
              class="p-5 bg-info-subtle rounded-4 h-100 d-flex flex-column justify-content-center align-items-start"
            >
              <h3 class="fw-bold">Descubre lo mejor de tu barrio</h3>
              <p class="mb-4">
                Encuentra comercios únicos, servicios de confianza y ofertas
                especiales cerca de ti.
              </p>
              <RouterLink to="/comercios" class="btn btn-primary">Explorar Comercios</RouterLink>
            </div>
          </div>
          <div class="col-md-6">
            <div
              class="p-5 bg-dark text-white rounded-4 h-100 d-flex flex-column justify-content-center align-items-start"
            >
              <h3 class="fw-bold text-white">¿Tienes un negocio?</h3>
              <p class="mb-4 text-white">
                Digitaliza tu comercio, llega a más vecinos y haz crecer tus
                ventas.
              </p>
              <RouterLink to="/login?tab=register" class="btn btn-light fw-bold text-dark">
                Regístrate Ahora
              </RouterLink>
            </div>
          </div>
        </div>
      </div>
    </section>
</template>

<style scoped>
  :root {
        --db-primary: #003366; 
        --db-secondary: #3a86ff; 
        --db-success: #28a745; 
        --db-danger: #dc3545; 
  }

  .card-hover {
    transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
  }
  .card-hover:hover {
    transform: translateY(-5px);
    box-shadow: 0 .5rem 1rem rgba(0,0,0,.15)!important;
  }
</style>