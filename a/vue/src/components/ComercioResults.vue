<script setup>
import { ref } from 'vue'
import ComercioCard from './ComercioCard.vue'

// --- CONFIGURACIÓN DE RUTAS E IMÁGENES INTEGRADAS ---
const API_URL = 'http://localhost:8080'

// Detecta automáticamente todas las imágenes procesadas por Vite en assets
const comercioImageModules = import.meta.glob('../assets/images/*.{png,jpg,jpeg,webp,svg,gif}', {
  eager: true,
  import: 'default',
})

const comercioImagesByName = Object.fromEntries(
  Object.entries(comercioImageModules).map(([path, url]) => [path.split('/').pop(), url]),
)

// Función inteligente idéntica a la de ComercioDetalleView
function normalizeImageUrl(imageUrl) {
  // 🌟 CORRECCIÓN: Si no hay imagen (o viene un 'null' en texto), usamos una foto por defecto real y segura
  if (!imageUrl || imageUrl === 'null' || imageUrl === '') {
    return 'https://images.unsplash.com/photo-1534723452862-4c874018d66d?q=80&w=600&auto=format&fit=crop'
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

// --- PROPS RECIBIDAS ---
defineProps({
  comercios: {
    type: Array,
    required: true,
    default: () => []
  },
  horaActual: {
    type: String,
    required: true
  },
  isComercioOpen: {
    type: Function,
    required: true
  }
})

// Métodos auxiliares de formateo
function formatRating(rating) {
  return Number(rating || 0).toFixed(1)
}
</script>

<template>
  <div class="container my-4">
    <div v-if="comercios.length" class="row g-4" id="comercioGrid">
      <ComercioCard
        v-for="comercio in comercios"
        :key="comercio.id"
        
        :image-url="normalizeImageUrl(comercio.logo)" 
        :image-alt="comercio.nombreComercio"
        :category="comercio.categoria"
        
        :is-opened="isComercioOpen(comercio, horaActual)"
        
        :name="comercio.nombreComercio"
        :star="formatRating(comercio.media || comercio.puntuacionMedia || 0)" 
        :opinions="'(' + (comercio.total || comercio.totalResenas || 0) + ')'" 
        :to="{ name: 'comercio-detalle', params: { id: comercio.id } }"
      />
    </div>

    <div v-else class="text-center py-5">
      <i class="bi bi-shop fs-1 text-muted mb-3 d-block"></i>
      <p class="text-muted fs-5">No se encontraron comercios que coincidan con la búsqueda.</p>
    </div>
  </div>
</template>

<style scoped>
#comercioGrid {
  transition: all 0.3s ease;
}
</style>

<style scoped>
.search-bar .form-control {
  border-color: #e0e0e0;
  box-shadow: none;
}

.search-bar .form-control:focus {
  border-color: var(--db-secondary);
  box-shadow: 0 0 0 0.2rem rgba(58, 134, 255, 0.12);
}

.border-muted {
  border-color: #e0e0e0 !important;
}

.commerce-search-shell {
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
}

.empty-state-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--db-secondary);
  background: rgba(58, 134, 255, 0.1);
}
</style>