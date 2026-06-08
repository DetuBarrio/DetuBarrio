<script setup>
import { computed, ref, watch } from 'vue'
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
import { apiUrl } from '../config/api'
import { isComercioOpen } from '../utils/comercioHelpers'
import OpenedBadge from '../components/OpenedBadge.vue'
import { showToast } from '../utils/toastService'

import { getAuth } from '../services/authService' 

const route = useRoute()
const comercio = ref(null)
const isLoading = ref(true)
const errorMessage = ref('')
const enviando = ref(false)

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

const esFavorito = ref(false)
// --- NUEVA LÓGICA DE FAVORITOS (CONECTADA AL BACKEND) ---
async function comprobarFavorito() {
  if (!comercio.value || !isLoggedIn.value) return
  
  try {
    const authData = getAuth()
    const token = authData?.token || localStorage.getItem('token')
    
    // Obtenemos la lista de favoritos del usuario para ver si este comercio está
    const response = await axios.get(apiUrl('/api/favoritos'), {
      headers: { Authorization: `Bearer ${token}` }
    })
    
    const listadoActual = response.data
    esFavorito.value = listadoActual.some(item => Number(item.id) === Number(comercio.value.id))
  } catch (error) {
    console.error("Error al comprobar favorito:", error)
  }
}

async function conmutarFavorito() {
  if (!comercio.value || !isLoggedIn.value) return
  
  try {
    const authData = getAuth()
    const token = authData?.token || localStorage.getItem('token')
    
    // El backend hace el trabajo sucio y nos devuelve "true" si se añadió o "false" si se quitó
    const response = await axios.post(apiUrl(`/api/favoritos/${comercio.value.id}`), {}, {
      headers: { Authorization: `Bearer ${token}` }
    })
    
    esFavorito.value = response.data
  } catch (error) {
    console.error("Error al conmutar favorito:", error)
    showToast('Hubo un error al actualizar tus favoritos.', 'error')
  }
}
// -----------------------------------------------------------------------

const comercioImageModules = import.meta.glob('../assets/images/*.{png,jpg,jpeg,webp,svg,gif}', {
  eager: true,
  import: 'default',
})

const comercioImagesByName = Object.fromEntries(
  Object.entries(comercioImageModules).map(([path, url]) => [path.split('/').pop(), url]),
)

const DEFAULT_IMAGE = comercioImagesByName.logo_og || comercioImagesByName['logo_og.png'] || '/images/logo_og.png'

function normalizeImageUrl(imageUrl) {
  if (!imageUrl) return DEFAULT_IMAGE
  if (typeof imageUrl === 'string' && imageUrl.startsWith('/uploads')) return apiUrl(imageUrl)
  if (/^https?:\/\//i.test(imageUrl) || imageUrl.startsWith('data:')) return imageUrl

  const fileName = imageUrl.split('/').pop()
  if (comercioImagesByName[fileName]) return comercioImagesByName[fileName]

  const cleanedImage = imageUrl.replace(/^\.\//, '').replace(/^images\//, '')
  const cleanedName = cleanedImage.split('/').pop()
  if (comercioImagesByName[cleanedName]) return comercioImagesByName[cleanedName]

  return imageUrl.startsWith('/') ? imageUrl : `/images/${cleanedName}`
}

const heroImage = computed(() => normalizeImageUrl(comercio.value?.banner || comercio.value?.logo))
const logoImage = computed(() => normalizeImageUrl(comercio.value?.logo || comercio.value?.banner))
const resenas = computed(() => comercio.value?.resenas || [])
const ratingValue = computed(() => Number(comercio.value?.puntuacionMedia || 0).toFixed(1))
const totalResenas = computed(() => Number(comercio.value?.totalResenas || 0))
const comercioIsOpen = computed(() => comercio.value ? isComercioOpen(comercio.value, new Date()) : false)

const ratingDistribution = computed(() => {
  const counts = [1, 2, 3, 4, 5].map((valoracion) => ({ valoracion, count: 0 }))
  for (const resena of resenas.value) {
    const entry = counts.find((item) => item.valoracion === Number(resena.valoracion))
    if (entry) entry.count += 1
  }
  return counts.reverse().map((item) => ({
    ...item,
    percentage: totalResenas.value ? Math.round((item.count / totalResenas.value) * 100) : 0,
  }))
})

function formatCurrency(value) {
  return new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' }).format(Number(value || 0))
}

function formatDate(value) {
  if (!value) return ''
  return new Intl.DateTimeFormat('es-ES', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value))
}

function buildStarIcons(value) {
  const rating = Number(value || 0)
  const fullStars = Math.floor(rating)
  return {
    fullStars,
    hasHalfStar: rating - fullStars >= 0.5,
    emptyStars: 5 - fullStars - (rating - fullStars >= 0.5 ? 1 : 0),
  }
}

function getInitials(name) {
  return name ? name.charAt(0).toUpperCase() : 'U'
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
    await comprobarFavorito() // Comprobamos en el backend si es favorito
  } catch (error) {
    console.error(error)
    errorMessage.value = 'No se pudo cargar el detalle del comercio.'
    comercio.value = null
  } finally {
    isLoading.value = false
  }
}

async function enviarResena() {
  if (nuevaResena.value.titulo.trim() === '' || nuevaResena.value.comentario.trim() === '') return
  enviando.value = true
  try {
    const authData = getAuth();
    const token = authData?.token || localStorage.getItem('token')
    await axios.post(
      apiUrl(`/api/comercios/${comercio.value.id}/resenas`),
      { titulo: nuevaResena.value.titulo, comentario: nuevaResena.value.comentario, valoracion: nuevaResena.value.valoracion },
      { headers: { Authorization: `Bearer ${token}` } }
    )
    nuevaResena.value = { titulo: '', comentario: '', valoracion: 5 }
    showToast('¡Tu opinión se ha compartido con éxito!', 'success')
    await loadComercio() 
  } catch (error) {
    console.error("Error al publicar la reseña:", error)
    showToast('No se pudo guardar la reseña.', 'error')
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
          <RouterLink class="btn btn-action-back rounded-pill mb-3 px-4 shadow-sm" to="/comercios">
            <i class="bi bi-arrow-left me-2"></i> Volver al listado
          </RouterLink>

          <div class="detail-hero card border-0 shadow-lg overflow-hidden rounded-5">
            <div class="position-relative">
              <img :src="heroImage" class="detail-hero-image w-100" :alt="comercio.nombreComercio" />
              
              <button 
                v-if="isLoggedIn"
                @click="conmutarFavorito" 
                class="btn btn-favorito-floating position-absolute top-0 end-0 m-4 shadow-lg rounded-circle d-flex align-items-center justify-content-center"
                :class="{ 'activo': esFavorito }"
                :title="esFavorito ? 'Quitar de favoritos' : 'Añadir a favoritos'"
                style="z-index: 5; width: 48px; height: 48px;"
              >
                <i class="bi fs-4" :class="esFavorito ? 'bi-heart-fill' : 'bi-heart'"></i>
              </button>

              <div class="detail-hero-overlay position-absolute bottom-0 start-0 w-100 p-4 p-lg-5 text-white">
                <div class="glass-content p-4 rounded-4 d-flex flex-wrap align-items-end justify-content-between gap-3">
                  <div>
                    <span class="badge bg-primary text-uppercase mb-2 tracking-wider px-3 py-1.5 fs-7 rounded-pill shadow-sm">
                      {{ comercio.categoria }}
                    </span>
                    <h1 class="display-5 fw-extrabold mb-1 mt-1 text-shadow-sm">{{ comercio.nombreComercio }}</h1>
                    <p class="mb-0 text-light opacity-90 fs-5">{{ comercio.descripcion }}</p>
                  </div>
                  <span class="badge rounded-pill bg-success-gradient px-4 py-2 fs-6 shadow-sm">
                    <i class="bi bi-patch-check-fill me-1"></i>
                    {{ comercio.puntuacionMedia >= 4 ? 'Muy valorado' : 'Comercio local' }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="row g-4 align-items-start">
          <div class="col-12 col-lg-8">
            <section class="card card-custom-layout border-0 shadow-sm rounded-4 p-4 mb-4">
              <div class="d-flex align-items-center gap-2 mb-3">
                <div class="icon-shape bg-primary-light text-primary rounded-3 p-2">
                  <i class="bi bi-shop fs-4"></i>
                </div>
                <h2 class="h4 fw-bold mb-0">Sobre nosotros</h2>
              </div>
              <p class="text-secondary mb-0 leading-relaxed">{{ comercio.descripcion || 'Este comercio todavía no ha añadido una descripción.' }}</p>
            </section>

            <SeccionReservas 
              v-if="comercio"
              :idComercio="comercio.id" 
              :disponibilidades="comercio.disponibilidades || []" 
              class="shadow-sm rounded-4 border-0 mb-4"
            />

            <section class="card card-custom-layout border-0 shadow-sm rounded-4 p-4 mt-4">
              <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
                <div>
                  <h2 class="h4 fw-bold mb-1"><i class="bi bi-chat-square-heart me-2 text-primary"></i>Opiniones de clientes</h2>
                  <p class="text-muted mb-0 fw-medium">{{ totalResenas }} reseñas publicadas</p>
                </div>
                <div class="d-flex align-items-center gap-3 bg-light p-3 rounded-4 shadow-inner">
                  <h3 class="display-5 fw-extrabold text-primary mb-0">{{ ratingValue }}</h3>
                  <div>
                    <div class="text-warning lh-1 fs-5">
                      <template v-for="index in buildStarIcons(comercio.puntuacionMedia).fullStars" :key="`full-${index}`">
                        <i class="bi bi-star-fill"></i>
                      </template>
                      <i v-if="buildStarIcons(comercio.puntuacionMedia).hasHalfStar" class="bi bi-star-half"></i>
                      <template v-for="index in buildStarIcons(comercio.puntuacionMedia).emptyStars" :key="`empty-${index}`">
                        <i class="bi bi-star"></i>
                      </template>
                    </div>
                    <p class="text-muted small fw-semibold mb-0 mt-1">Valoración media</p>
                  </div>
                </div>
              </div>
              
              <div class="bg-light p-3 rounded-4 mb-4">
                <div v-for="item in ratingDistribution" :key="item.valoracion" class="d-flex align-items-center gap-2 mb-2">
                  <span class="small text-secondary fw-bold rating-label">{{ item.valoracion }} ★</span>
                  <div class="rating-bar flex-grow-1">
                    <div class="rating-bar-fill" :style="{ width: `${item.percentage}%` }"></div>
                  </div>
                  <span class="small text-muted fw-semibold rating-percentage">{{ item.percentage }}%</span>
                </div>
              </div>

              <div v-if="isLoggedIn" class="card p-4 mb-4 border-0 bg-form-light rounded-4 shadow-sm">
                <h4 class="h6 fw-bold text-dark mb-3"><i class="bi bi-pencil-square me-2 text-primary"></i>Dejar una valoración</h4>
                <form @submit.prevent="enviarResena">
                  
                  <div class="mb-3">
                    <label class="form-label small fw-bold text-secondary d-block mb-1">Tu puntuación:</label>
                    <div class="stars-selector text-warning fs-3">
                      <i v-for="i in 5" :key="i"
                         :class="i <= nuevaResena.valoracion ? 'bi bi-star-fill me-1' : 'bi bi-star me-1'"
                         @click="nuevaResena.valoracion = i"
                         style="cursor: pointer;"></i>
                    </div>
                  </div>

                  <div class="mb-3">
                    <label class="form-label small fw-bold text-secondary">Título breve</label>
                    <input v-model="nuevaResena.titulo" type="text" class="form-control rounded-3 custom-input" 
                           placeholder="Ej: ¡Excelente servicio!, Muy recomendado..." required>
                  </div>

                  <div class="mb-3">
                    <label class="form-label small fw-bold text-secondary">Tu comentario</label>
                    <textarea v-model="nuevaResena.comentario" class="form-control rounded-3 custom-input" rows="3" 
                              placeholder="Cuéntanos tu experiencia con este comercio..." required></textarea>
                  </div>

                  <button type="submit" class="btn btn-primary px-4 fw-bold rounded-3 shadow-sm btn-submit-review" :disabled="enviando">
                    <span v-if="enviando"><span class="spinner-border spinner-border-sm me-2"></span>Enviando...</span>
                    <span v-else><i class="bi bi-send-fill me-2"></i> Publicar Reseña</span>
                  </button>
                </form>
              </div>

              <div v-else class="alert alert-light border-dashed text-center rounded-4 p-4 mb-4">
                <i class="bi bi-lock-fill text-muted fs-3 d-block mb-2"></i>
                <span class="h6 fw-bold text-dark mb-1 d-block">¿Quieres dejar una opinión?</span>
                <p class="text-muted small mb-3">Debes iniciar sesión en tu cuenta para poder valorar este comercio.</p>
                <RouterLink to="/login" class="btn btn-dark btn-sm px-4 fw-bold rounded-pill shadow-sm">
                  <i class="bi bi-box-arrow-in-right me-1"></i> Iniciar Sesión
                </RouterLink>
              </div>

              <div class="resenas-scroll-container pr-2" id="resenasList">
                <div v-for="resena in resenas" :key="resena.id" class="resena-card-custom p-3 mb-3 bg-white border shadow-xs rounded-4">
                  <div class="d-flex justify-content-between align-items-start gap-3 mb-2">
                    <div class="d-flex align-items-center gap-2.5">
                      <div class="avatar-review-placeholder d-flex align-items-center justify-content-center text-white fw-bold rounded-circle">
                        {{ getInitials(resena.autorNombre) }}
                      </div>
                      <div>
                        <h3 class="h6 fw-bold mb-0 text-dark-emphasis">{{ resena.titulo }}</h3>
                        <p class="text-muted small mb-0 fw-medium">
                          {{ resena.autorNombre }} <span v-if="resena.autorEmail" class="opacity-75">· {{ resena.autorEmail }}</span>
                        </p>
                      </div>
                    </div>
                    <span class="badge badge-rating-pill px-2.5 py-1.5 fw-bold fs-7 rounded-3">
                      {{ resena.valoracion }} / 5 <i class="bi bi-star-fill text-warning ms-1"></i>
                    </span>
                  </div>
                  <p class="mb-2 text-secondary-emphasis fs-6 leading-relaxed ps-1">{{ resena.comentario || 'Sin comentario.' }}</p>
                  <div class="d-flex justify-content-end">
                    <small class="text-muted fs-7 fw-medium"><i class="bi bi-clock me-1"></i>{{ formatDate(resena.fecha) }}</small>
                  </div>
                </div>
                <div v-if="!resenas.length" class="text-muted p-4 text-center border rounded-4 bg-light">
                  <i class="bi bi-chat-left-text d-block fs-3 mb-2 text-neutral"></i>
                  Aún no hay reseñas para este comercio. ¡Sé el primero en opinar!
                </div>
              </div>

            </section>
          </div>

          <div class="col-12 col-lg-4">
            <div class="sticky-top detail-sidebar" style="top: 24px;">
              
              <div class="card border-0 shadow-sm rounded-4 p-4 mb-4 card-sidebar-custom">
                <div class="d-flex align-items-center gap-3 mb-4 border-bottom pb-3">
                  <img :src="logoImage" class="rounded-4 detail-logo border p-1 bg-white shadow-xs" :alt="comercio.nombreComercio" />
                  <div>
                    <span class="text-primary small fw-bold text-uppercase tracking-wide">{{ comercio.categoria }}</span>
                    <h2 class="h5 fw-extrabold mb-0 text-dark-emphasis mt-0.5">{{ comercio.nombreComercio }}</h2>
                  </div>
                </div>

                <RouterLink to="/contacto" class="btn btn-contact-gradient w-100 rounded-3 mb-4 py-2.5 text-center fw-bold text-white shadow-sm">
                  <i class="bi bi-envelope-paper-fill me-2"></i>Contactar con Dtubarrio
                </RouterLink>

                <div class="mb-4">
                  <p class="small text-uppercase text-muted fw-bold tracking-wider mb-2.5"><i class="bi bi-clock-history me-1.5 text-primary"></i>Horario</p>
                  <div class="bg-light p-3 rounded-3 border-start border-primary border-3">
                    <p class="mb-1 small fw-semibold text-dark">{{ comercio.horario || 'Horario no disponible' }}</p>
                    <p class="mb-0 small text-muted">{{ comercio.diasApertura || 'Días de apertura no disponibles' }}</p>
                  </div>
                </div>

                <div>
                  <p class="small text-uppercase text-muted fw-bold tracking-wider mb-2"><i class="bi bi-info-circle me-1.5 text-primary"></i>Estado</p>
                  <div class="d-flex align-items-center gap-2 p-2.5 rounded-3" :class="comercioIsOpen ? 'bg-success-light' : 'bg-danger-light'">
                    <OpenedBadge :state="comercioIsOpen" />
                    <span class="small" :class="comercioIsOpen ? 'text-success-emphasis' : 'text-danger-emphasis'" style="font-weight: 500;">Información orientativa</span>
                  </div>
                </div>
              </div>
              
              <div class="card border-0 shadow-sm p-4 card-sidebar-custom" style="border-radius: 16px;">
                <h4 class="h6 fw-bold text-dark mb-3"><i class="bi bi-geo-alt-fill me-2 text-danger"></i>📍 Ubicación</h4>
                <p class="text-secondary small mb-3" style="line-height: 1.5;">
                  <strong class="text-dark">Dirección/Indicaciones:</strong><br>
                  {{ comercio.ubicacion || 'Dirección no especificada por el comercio.' }}
                </p>
                <div v-if="comercio.ubicacion">
                  <a :href="'https://www.google.com/maps/search/?api=1&query=' + encodeURIComponent(comercio.ubicacion)" 
                    target="_blank" 
                    class="btn btn-outline-primary w-100 fw-bold d-flex align-items-center justify-content-center gap-2 btn-maps-custom py-2">
                    <i class="bi bi-map-fill"></i> Ver en Google Maps
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
/* Estilos del nuevo botón flotante de favoritos */
.btn-favorito-floating {
  background-color: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(4px);
  border: 1px solid rgba(0, 0, 0, 0.05);
  color: #64748b;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}
.btn-favorito-floating:hover {
  transform: scale(1.1);
  background-color: #ffffff;
  color: #dc3545;
}
.btn-favorito-floating.activo {
  background-color: #ffffff;
  color: #dc3545;
  border-color: rgba(220, 53, 69, 0.2);
}

.detail-page {
  background:
    radial-gradient(circle at top left, rgba(58, 134, 255, 0.05), transparent 35%),
    radial-gradient(circle at bottom right, rgba(103, 164, 255, 0.04), transparent 40%),
    linear-gradient(180deg, #f8fafd 0%, #ffffff 100%);
  min-height: 100vh;
}

.card-custom-layout, .card-sidebar-custom {
  background-color: #ffffff;
  border: 1px solid rgba(226, 232, 240, 0.7) !important;
}

.detail-hero-image {
  height: 380px;
  object-fit: cover;
}

.detail-hero-overlay {
  background: linear-gradient(180deg, transparent 10%, rgba(15, 23, 42, 0.85) 100%);
}

.glass-content {
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.text-shadow-sm {
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.fw-extrabold {
  font-weight: 800;
}

.btn-action-back {
  background-color: #ffffff;
  color: #475569;
  border: 1px solid #e2e8f0;
  font-weight: 600;
  transition: all 0.2s ease;
}
.btn-action-back:hover {
  background-color: #f1f5f9;
  color: #1e293b;
  transform: translateY(-1px);
}

.btn-contact-gradient {
  background: linear-gradient(135deg, #3a86ff 0%, #2563eb 100%);
  border: none;
  transition: all 0.2s ease;
}
.btn-contact-gradient:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.25);
}

.bg-success-gradient {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.resenas-scroll-container {
  max-height: 520px;
  overflow-y: auto;
  padding-right: 8px;
}

.resenas-scroll-container::-webkit-scrollbar {
  width: 6px;
}
.resenas-scroll-container::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 10px;
}
.resenas-scroll-container::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}
.resenas-scroll-container::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

.resena-card-custom {
  border: 1px solid #e2e8f0 !important;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.resena-card-custom:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03) !important;
  border-color: #cbd5e1 !important;
}

.avatar-review-placeholder {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #64748b 0%, #475569 100%);
  font-size: 1rem;
  flex-shrink: 0;
}

.badge-rating-pill {
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #334155;
}

.bg-form-light {
  background-color: #f8fafc;
  border: 1px solid #e2e8f0 !important;
}
.custom-input {
  border: 1px solid #cbd5e1;
  padding: 0.6rem 0.75rem;
  transition: all 0.2s ease;
}
.custom-input:focus {
  border-color: #3a86ff;
  box-shadow: 0 0 0 3px rgba(58, 134, 255, 0.15);
}

.detail-logo {
  width: 72px;
  height: 72px;
  object-fit: cover;
}

.bg-primary-light {
  background-color: rgba(58, 134, 255, 0.1);
}

.bg-success-light {
  background-color: rgba(16, 185, 129, 0.08);
}

.rating-bar {
  height: 8px;
  border-radius: 999px;
  background: #e2e8f0;
  overflow: hidden;
}

.rating-bar-fill {
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, #3a86ff 0%, #60a5fa 100%);
}

.rating-label {
  min-width: 32px;
}
.rating-percentage {
  min-width: 36px;
  text-align: right;
}

.stars-selector i {
  transition: transform 0.1s ease;
  display: inline-block;
}
.stars-selector i:hover {
  transform: scale(1.2);
}

.detail-sidebar {
  z-index: 10;
}

.border-dashed {
  border: 2px dashed #e2e8f0 !important;
}
</style>