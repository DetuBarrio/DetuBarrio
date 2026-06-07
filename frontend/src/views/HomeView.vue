<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import fondo from '../assets/images/fondo.png';
import comercioDefault from '../assets/images/buenaMesa.png'; 
import { apiUrl } from '../config/api'

const router = useRouter()
const searchTerm = ref('')
const comerciosDestacados = ref([])
const carouselRef = ref(null)
let carouselInstance = null

const comercioImageModules = import.meta.glob('../assets/images/*.{png,jpg,jpeg,webp,svg,gif}', {
  eager: true,
  import: 'default',
})

const comercioImagesByName = Object.fromEntries(
  Object.entries(comercioImageModules).map(([path, url]) => [path.split('/').pop(), url]),
)

const gruposDestacados = computed(() => {
  const grupos = []
  for (let i = 0; i < comerciosDestacados.value.length; i += 4) {
    grupos.push(comerciosDestacados.value.slice(i, i + 4))
  }
  return grupos
})

function getImagenComercio(imageUrl) {
  if (!imageUrl) {
    return comercioDefault
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

function initCarousel() {
  nextTick(() => {
    if (carouselInstance) {
      carouselInstance.dispose()
    }
    const el = document.getElementById('comerciosCarousel')
    if (el) {
      const Carousel = window.bootstrap?.Carousel
      if (Carousel) {
        carouselInstance = new Carousel(el, {
          interval: 5000,
          ride: 'carousel',
          wrap: true,
        })
      }
    }
  })
}

onMounted(async () => {
  try {
    const response = await fetch(apiUrl('/api/comercios?page=0&size=50'))
    if (response.ok) {
      const data = await response.json()
      const lista = Array.isArray(data) ? data : (data.content || [])

      comerciosDestacados.value = lista
        .sort((a, b) => (b.media || b.puntuacionMedia || 0) - (a.media || a.puntuacionMedia || 0))
        .slice(0, 10)

      initCarousel()
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
        <div class="d-flex align-items-center justify-content-between mb-4">
          <h3 class="fw-bold mb-0">Nuestros Comercios Destacados</h3>
          <RouterLink to="/comercios" class="btn btn-outline-primary rounded-pill btn-sm">
            Ver todos <i class="bi bi-arrow-right ms-1"></i>
          </RouterLink>
        </div>

        <div v-if="comerciosDestacados.length === 0" class="text-center text-muted py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Cargando...</span>
          </div>
        </div>

        <div v-else>
          <div id="comerciosCarousel" class="carousel slide" ref="carouselRef">
            <div class="carousel-inner">
              <div
                v-for="(grupo, index) in gruposDestacados"
                :key="index"
                class="carousel-item"
                :class="{ active: index === 0 }"
              >
                <div class="row g-4">
                  <div
                    v-for="comercio in grupo"
                    :key="comercio.id"
                    class="col-12 col-sm-6 col-lg-3"
                  >
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
                              <i
                                class="bi"
                                :class="n <= Math.round(comercio.media || comercio.puntuacionMedia || 0) ? 'bi-star-fill' : 'bi-star text-muted'"
                              ></i>
                            </template>
                            <span class="text-muted ms-1">
                              {{ (comercio.media || comercio.puntuacionMedia) ? Number(comercio.media || comercio.puntuacionMedia).toFixed(1) : '0.0' }}
                              ({{ comercio.total || comercio.totalResenas || 0 }})
                            </span>
                          </div>
                        </div>
                      </div>
                    </RouterLink>
                  </div>
                </div>
              </div>
            </div>

            <button
              class="carousel-control-prev"
              type="button"
              data-bs-target="#comerciosCarousel"
              data-bs-slide="prev"
            >
              <span class="carousel-control-prev-icon carousel-btn-custom" aria-hidden="true"></span>
              <span class="visually-hidden">Anterior</span>
            </button>
            <button
              class="carousel-control-next"
              type="button"
              data-bs-target="#comerciosCarousel"
              data-bs-slide="next"
            >
              <span class="carousel-control-next-icon carousel-btn-custom" aria-hidden="true"></span>
              <span class="visually-hidden">Siguiente</span>
            </button>
          </div>

          <div class="d-flex justify-content-center gap-2 mt-3">
            <button
              v-for="(grupo, index) in gruposDestacados"
              :key="index"
              type="button"
              class="carousel-dot"
              :class="{ active: false }"
              :data-bs-target="'#comerciosCarousel'"
              :data-bs-slide-to="index"
              :aria-label="'Ir a slide ' + (index + 1)"
            ></button>
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

  .carousel-btn-custom {
    background-color: rgba(58, 134, 255, 0.8);
    border-radius: 50%;
    padding: 1.2rem;
    width: 48px;
    height: 48px;
    background-size: 1.2rem;
  }

  .carousel-control-prev:hover .carousel-btn-custom,
  .carousel-control-next:hover .carousel-btn-custom {
    background-color: rgba(58, 134, 255, 1);
  }

  .carousel-dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    border: 2px solid #3a86ff;
    background: transparent;
    padding: 0;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .carousel-dot.active {
    background: #3a86ff;
  }
</style>