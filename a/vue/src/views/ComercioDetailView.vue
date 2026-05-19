<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { fetchComercioById } from '../services/comercioService'
import axios from 'axios' 
import panaderiaPan from '../assets/images/panaderia_pan.png'
import panaderiaCruasant from '../assets/images/panaderia_cruasant.png'
import saborImage from '../assets/images/sabor.png'
import torfelizImage from '../assets/images/torfeliz.png'
import fontaneroImage from '../assets/images/fontanero.png'
import buenaMesaImage from '../assets/images/buenaMesa.png'
import SeccionReservas from './SeccionReservas.vue'

// 🎯 IMPORTACIÓN CORREGIDA: Traemos getAuth para usar la misma lógica que las reservas
import { getAuth } from '../services/authService' 

const route = useRoute()
const comercio = ref(null)
const isLoading = ref(true)
const errorMessage = ref('')

// --- VARIABLES PARA EL FORMULARIO DE RESEÑAS ---
const enviando = ref(false)

// 🎯 LÓGICA CORREGIDA: Idéntica a la que usas en SeccionReservas.vue
const isLoggedIn = computed(() => {
  const authData = getAuth();
  const token = authData?.token || localStorage.getItem('token');
  const usuarioId = authData?.id || authData?.usuarioId || localStorage.getItem('usuarioId');
  return !!(token && usuarioId);
})

const nuevaResena = ref({
  titulo: '',
  comentario: '',
  valoracion: 5
})
// -----------------------------------------------------------------------

// Configuración del servidor Backend
const API_URL = 'http://localhost:8080'

const comercioImageModules = import.meta.glob('../assets/images/*.{png,jpg,jpeg,webp,svg,gif}', {
  eager: true,
  import: 'default',
})

const comercioImagesByName = Object.fromEntries(
  Object.entries(comercioImageModules).map(([path, url]) => [path.split('/').pop(), url]),
)

const DEFAULT_IMAGE = comercioImagesByName.logo_og || comercioImagesByName['logo_og.png'] || '/images/logo_og.png'

const PRODUCT_IMAGE_CATALOG = {
  'panaderia el trigal': {
    'pan de masa madre': panaderiaPan,
    'croissant de mantequilla': panaderiaCruasant,
    default: panaderiaPan,
  },
  'el sabor casero': {
    'menu del dia': buenaMesaImage,
    default: saborImage,
  },
  'el tornillo feliz': {
    'kit basico bricolaje': fontaneroImage,
    default: torfelizImage,
  },
}

const FALLBACK_PRODUCT_IMAGES = [
  panaderiaPan,
  panaderiaCruasant,
  buenaMesaImage,
  saborImage,
  fontaneroImage,
  torfelizImage,
]

function normalizeImageUrl(imageUrl) {
  if (!imageUrl) {
    return DEFAULT_IMAGE
  }

  if (typeof imageUrl === 'string' && imageUrl.startsWith('/uploads')) {
    return `${API_URL}${imageUrl}`
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

function getProductoImage(producto, index = 0) {
  const comercioKey = (comercio.value?.nombreComercio || '').trim().toLowerCase()
  const productoKey = (producto?.nombreProducto || '').trim().toLowerCase()
  const comercioCatalog = PRODUCT_IMAGE_CATALOG[comercioKey]

  if (comercioCatalog) {
    if (comercioCatalog[productoKey]) {
      return comercioCatalog[productoKey]
    }

    return comercioCatalog.default || FALLBACK_PRODUCT_IMAGES[index % FALLBACK_PRODUCT_IMAGES.length]
  }

  if (producto?.imagen) {
    return normalizeImageUrl(producto.imagen)
  }

  return FALLBACK_PRODUCT_IMAGES[index % FALLBACK_PRODUCT_IMAGES.length]
}

const heroImage = computed(() => normalizeImageUrl(comercio.value?.banner || comercio.value?.logo))
const logoImage = computed(() => normalizeImageUrl(comercio.value?.logo || comercio.value?.banner))
const productos = computed(() => comercio.value?.productos || [])
const resenas = computed(() => comercio.value?.resenas || [])
const ratingValue = computed(() => Number(comercio.value?.puntuacionMedia || 0).toFixed(1))
const totalResenas = computed(() => Number(comercio.value?.totalResenas || 0))

const ratingDistribution = computed(() => {
  const counts = [1, 2, 3, 4, 5].map((valoracion) => ({ valoracion, count: 0 }))

  for (const resena of resenas.value) {
    const entry = counts.find((item) => item.valoracion === Number(resena.valoracion))
    if (entry) {
      entry.count += 1
    }
  }

  return counts.reverse().map((item) => ({
    ...item,
    percentage: totalResenas.value ? Math.round((item.count / totalResenas.value) * 100) : 0,
  }))
})

function formatCurrency(value) {
  const amount = Number(value || 0)
  return new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' }).format(amount)
}

function formatDate(value) {
  if (!value) {
    return ''
  }

  return new Intl.DateTimeFormat('es-ES', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value))
}

function buildStarIcons(value) {
  const rating = Number(value || 0)
  const fullStars = Math.floor(rating)
  const hasHalfStar = rating - fullStars >= 0.5
  const emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0)

  return {
    fullStars,
    hasHalfStar,
    emptyStars,
  }
}

async function loadComercio() {
  const comercioId = Number(route.params.id)

  if (!Number.isFinite(comercioId) || comercioId <= 0) {
    errorMessage.value = 'El identificador del comercio no es válido.'
    isLoading.value = false
    return
  }

  isLoading.value = true
  errorMessage.value = ''

  try {
    comercio.value = await fetchComercioById(comercioId)
  } catch (error) {
    console.error(error)
    errorMessage.value = 'No se pudo cargar el detalle del comercio. Revisa que la API esté arrancada.'
    comercio.value = null
  } finally {
    isLoading.value = false
  }
}

async function enviarResena() {
  if (nuevaResena.value.titulo.trim() === '' || nuevaResena.value.comentario.trim() === '') return

  enviando.value = true
  try {
    // 🎯 OBTENCIÓN DEL TOKEN CORREGIDA: Asegura obtener el token activo real
    const authData = getAuth();
    const token = authData?.token || localStorage.getItem('token')
    const comercioId = comercio.value.id

    await axios.post(
      `${API_URL}/api/comercios/${comercioId}/resenas`,
      {
        titulo: nuevaResena.value.titulo,
        comentario: nuevaResena.value.comentario,
        valoracion: nuevaResena.value.valoracion
      },
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    )

    nuevaResena.value = { titulo: '', comentario: '', valoracion: 5 }
    alert('¡Tu opinión se ha compartido con éxito!')
    await loadComercio() 
  } catch (error) {
    console.error("Error al publicar la reseña:", error)
    alert('No se pudo guardar la reseña. Revisa que tu sesión siga activa.')
  } finally {
    enviando.value = false
  }
}

watch(() => route.params.id, loadComercio, { immediate: true })
</script>

<template>
  <div class="detail-page py-4 py-lg-5">
    <div class="container container-xl">
      <div v-if="isLoading" class="d-flex justify-content-center py-5">
        <div class="text-center">
          <div class="spinner-border text-primary" role="status" aria-hidden="true"></div>
          <p class="mt-3 mb-0 text-muted">Cargando comercio...</p>
        </div>
      </div>

      <div v-else-if="errorMessage" class="alert alert-danger border-0 shadow-sm" role="alert">
        {{ errorMessage }}
        <div class="mt-3">
          <RouterLink class="btn btn-light fw-semibold" to="/comercios">Volver al listado</RouterLink>
        </div>
      </div>

      <template v-else-if="comercio">
        <div class="mb-4">
          <RouterLink class="btn btn-outline-secondary rounded-pill mb-3" to="/comercios">
            <i class="bi bi-arrow-left me-2"></i> Volver al listado
          </RouterLink>

          <div class="detail-hero card border-0 shadow-sm overflow-hidden rounded-5">
            <div class="position-relative">
              <img :src="heroImage" class="detail-hero-image w-100" :alt="comercio.nombreComercio" />
              <div class="detail-hero-overlay position-absolute bottom-0 start-0 w-100 p-4 p-lg-5 text-white">
                <div class="d-flex flex-wrap align-items-end justify-content-between gap-3">
                  <div>
                    <p class="text-uppercase small fw-semibold mb-2 opacity-75">{{ comercio.categoria }}</p>
                    <h1 class="display-6 fw-bold mb-1">{{ comercio.nombreComercio }}</h1>
                    <p class="mb-0 opacity-90">{{ comercio.descripcion }}</p>
                  </div>
                  <span class="badge rounded-pill bg-success px-3 py-2">
                    {{ comercio.puntuacionMedia >= 4 ? 'Muy valorado' : 'Comercio local' }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="row g-4 align-items-start">
          <div class="col-12 col-lg-8">
            <section class="card border-0 shadow-sm rounded-4 p-4 mb-4">
              <h2 class="h4 fw-bold mb-3">Sobre nosotros</h2>
              <p class="text-muted mb-0">{{ comercio.descripcion || 'Este comercio todavía no ha añadido una descripción.' }}</p>
            </section>

            <section class="mb-4">
              <div class="d-flex align-items-center justify-content-between gap-3 mb-3">
                <h2 class="h4 fw-bold mb-0">Nuestros productos estrella</h2>
                <span class="text-muted small">{{ productos.length }} productos</span>
              </div>

              <div class="row g-3">
                <div v-for="(producto, index) in productos" :key="producto.id || producto.nombreProducto" class="col-12 col-md-6">
                  <div class="card border-0 shadow-sm rounded-4 h-100 overflow-hidden product-card">
                    <img :src="getProductoImage(producto, index)" class="product-card-image" :alt="producto.nombreProducto" />
                    <div class="card-body">
                      <h3 class="h6 fw-bold mb-1">{{ producto.nombreProducto }}</h3>
                      <p class="text-muted small mb-2">{{ producto.descripcion || 'Sin descripción' }}</p>
                      <p class="fw-semibold mb-0">{{ formatCurrency(producto.precio) }}</p>
                    </div>
                  </div>
                </div>
                <div v-if="!productos.length" class="col-12">
                  <div class="alert alert-light border mb-0">Este comercio aún no tiene productos publicados.</div>
                </div>
              </div>
            </section>

            <SeccionReservas 
              v-if="comercio"
              :idComercio="comercio.id" 
              :disponibilidades="comercio.disponibilidades || []" 
            />

            <section class="card border-0 shadow-sm rounded-4 p-4 mt-4">
              <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
                <div>
                  <h2 class="h4 fw-bold mb-1">Opiniones de clientes</h2>
                  <p class="text-muted mb-0">{{ totalResenas }} reseñas publicadas</p>
                </div>
                <div class="d-flex align-items-center gap-3">
                  <h3 class="display-6 fw-bold mb-0">{{ ratingValue }}</h3>
                  <div>
                    <div class="text-warning lh-1">
                      <template v-for="index in buildStarIcons(comercio.puntuacionMedia).fullStars" :key="`full-${index}`">
                        <i class="bi bi-star-fill"></i>
                      </template>
                      <i v-if="buildStarIcons(comercio.puntuacionMedia).hasHalfStar" class="bi bi-star-half"></i>
                      <template v-for="index in buildStarIcons(comercio.puntuacionMedia).emptyStars" :key="`empty-${index}`">
                        <i class="bi bi-star"></i>
                      </template>
                    </div>
                    <p class="text-muted small mb-0">Valoración media</p>
                  </div>
                </div>
              </div>
              
              <div class="mb-4">
                <div v-for="item in ratingDistribution" :key="item.valoracion" class="d-flex align-items-center gap-2 mb-2">
                  <span class="small text-muted rating-label">{{ item.valoracion }}</span>
                  <div class="rating-bar flex-grow-1">
                    <div class="rating-bar-fill" :style="{ width: `${item.percentage}%` }"></div>
                  </div>
                  <span class="small text-muted rating-percentage">{{ item.percentage }}%</span>
                </div>
              </div>

              <div v-if="isLoggedIn" class="card p-3 mb-4 border-0 bg-light rounded-4 shadow-sm">
                <h4 class="h6 fw-bold text-dark mb-3"><i class="bi bi-pencil-square me-2 text-primary"></i>Dejar una valoración</h4>
                <form @submit.prevent="enviarResena">
                  
                  <div class="mb-3">
                    <label class="form-label small fw-semibold text-secondary d-block mb-1">Tu puntuación:</label>
                    <div class="stars-selector text-warning fs-4">
                      <i v-for="i in 5" :key="i"
                         :class="i <= nuevaResena.valoracion ? 'bi bi-star-fill me-1' : 'bi bi-star me-1'"
                         @click="nuevaResena.valoracion = i"
                         style="cursor: pointer;"></i>
                    </div>
                  </div>

                  <div class="mb-3">
                    <label class="form-label small fw-semibold text-secondary">Título breve</label>
                    <input v-model="nuevaResena.titulo" type="text" class="form-control form-control-sm rounded-3" 
                           placeholder="Ej: ¡Excelente servicio!, Muy recomendado..." required>
                  </div>

                  <div class="mb-3">
                    <label class="form-label small fw-semibold text-secondary">Tu comentario</label>
                    <textarea v-model="nuevaResena.comentario" class="form-control form-control-sm rounded-3" rows="3" 
                              placeholder="Cuéntanos tu experiencia con este comercio..." required></textarea>
                  </div>

                  <button type="submit" class="btn btn-primary btn-sm px-4 fw-bold rounded-3" :disabled="enviando">
                    <span v-if="enviando"><span class="spinner-border spinner-border-sm me-2"></span>Enviando...</span>
                    <span v-else><i class="bi bi-send me-1"></i> Publicar Reseña</span>
                  </button>
                </form>
              </div>

              <div v-else class="alert alert-light text-center border rounded-4 p-4 mb-4">
                <i class="bi bi-lock-fill text-muted fs-3 d-block mb-2"></i>
                <h5 class="h6 fw-bold text-dark">¿Quieres dejar una opinión?</h5>
                <p class="text-muted small mb-3">Debes iniciar sesión en tu cuenta para poder valorar este comercio.</p>
                <RouterLink to="/login" class="btn btn-dark btn-sm px-4 fw-bold rounded-pill">
                  <i class="bi bi-box-arrow-in-right me-1"></i> Iniciar Sesión
                </RouterLink>
              </div>

              <div class="d-grid gap-3" id="resenasList">
                <div v-for="resena in resenas" :key="resena.id" class="border rounded-4 p-3 bg-white">
                  <div class="d-flex justify-content-between align-items-start gap-3 mb-2">
                    <div>
                      <h3 class="h6 fw-bold mb-1">{{ resena.titulo }}</h3>
                      <p class="text-muted small mb-0">{{ resena.autorNombre }}<span v-if="resena.autorEmail"> · {{ resena.autorEmail }}</span></p>
                    </div>
                    <span class="badge text-bg-primary">{{ resena.valoracion }}/5</span>
                  </div>
                  <p class="mb-2 text-body-secondary">{{ resena.comentario || 'Sin comentario.' }}</p>
                  <small class="text-muted">{{ formatDate(resena.fecha) }}</small>
                </div>
                <div v-if="!resenas.length" class="text-muted">Aún no hay reseñas para este comercio.</div>
              </div>
            </section>
          </div>

          <div class="col-12 col-lg-4">
            <div class="sticky-top detail-sidebar" style="top: 20px;">
              <div class="card border-0 shadow-sm rounded-4 p-4 mb-3">
                <div class="d-flex align-items-center gap-3 mb-3">
                  <img :src="logoImage" class="rounded-4 detail-logo" :alt="comercio.nombreComercio" />
                  <div>
                    <p class="text-muted small mb-1">{{ comercio.categoria }}</p>
                    <h2 class="h5 fw-bold mb-0">{{ comercio.nombreComercio }}</h2>
                  </div>
                </div>
                <button class="btn btn-primary w-100 rounded-3 mb-3">Contactar ahora</button>
                <div class="mb-3">
                  <p class="small text-uppercase text-muted fw-semibold mb-2">Horario</p>
                  <p class="mb-1 small">{{ comercio.horario || 'Horario no disponible' }}</p>
                  <p class="mb-0 small">{{ comercio.diasApertura || 'Días de apertura no disponibles' }}</p>
                </div>
                <div>
                  <p class="small text-uppercase text-muted fw-semibold mb-2">Estado</p>
                  <div class="d-flex align-items-center gap-2">
                    <span class="badge rounded-pill bg-success">Abierto</span>
                    <span class="small text-muted">Información orientativa</span>
                  </div>
                </div>
              </div>
              
              <div class="card shadow-sm p-3 mt-3" style="border-radius: 12px; background-color: #ffffff;">
                <h4 class="h6 fw-bold text-dark mb-3">📍 Ubicación</h4>
                <p class="text-muted small mb-2" style="line-height: 1.4;">
                  <strong>Dirección/Indicaciones:</strong><br>
                  {{ comercio.ubicacion || 'Dirección no especificada por el comercio.' }}
                </p>
                <div v-if="comercio.ubicacion" class="mt-2">
                  <a :href="'https://www.google.com/maps/search/?api=1&query=' + encodeURIComponent(comercio.ubicacion)" 
                    target="_blank" 
                    class="btn btn-outline-primary btn-sm w-100 fw-bold d-flex align-items-center justify-content-center gap-1">
                    🌐 Ver en Google Maps
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.detail-page {
  background:
    radial-gradient(circle at top left, rgba(58, 134, 255, 0.08), transparent 30%),
    linear-gradient(180deg, #fbfdff 0%, #ffffff 55%);
}

.detail-hero-image {
  height: 360px;
  object-fit: cover;
}

.detail-hero-overlay {
  background: linear-gradient(180deg, transparent 0%, rgba(5, 25, 40, 0.82) 100%);
}

.product-card-image {
  height: 170px;
  width: 100%;
  object-fit: cover;
}

.detail-logo {
  width: 72px;
  height: 72px;
  object-fit: cover;
}

.rating-bar {
  height: 10px;
  border-radius: 999px;
  background: #e9ecef;
  overflow: hidden;
}

.rating-bar-fill {
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, #3a86ff 0%, #67a4ff 100%);
}

.rating-label,
.rating-percentage {
  min-width: 24px;
}

.detail-sidebar {
  z-index: 10;
}

.detail-hero-overlay,
.detail-hero-overlay h1,
.detail-hero-overlay p,
.detail-hero-overlay .text-uppercase {
  color: #ffffff !important;
}

.stars-selector i {
  transition: transform 0.1s ease;
}
.stars-selector i:hover {
  transform: scale(1.2);
}
</style>