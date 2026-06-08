<script setup>
import { ref } from 'vue'
import ComercioCard from './ComercioCard.vue'
import { apiUrl } from '../config/api'

const comercioImageModules = import.meta.glob('../assets/images/*.{png,jpg,jpeg,webp,svg,gif}', {
  eager: true,
  import: 'default',
})

const comercioImagesByName = Object.fromEntries(
  Object.entries(comercioImageModules).map(([path, url]) => [path.split('/').pop(), url]),
)

const DEFAULT_IMAGE = comercioImagesByName.logo_og || comercioImagesByName['logo_og.png'] || '/images/logo_og.png'

function normalizeImageUrl(imageUrl) {
  if (!imageUrl || imageUrl === 'null' || imageUrl === '') {
    return DEFAULT_IMAGE
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

const props = defineProps({
  comercios: {
    type: Array,
    required: true,
    default: () => []
  },
  searchQuery: {
    type: String,
    default: ''
  },
  totalResultados: {
    type: Number,
    default: 0
  },
  horaActual: {
    type: String,
    required: true
  },
  isComercioOpen: {
    type: Function,
    required: true
  },
  currentPage: {
    type: Number,
    default: 0
  },
  totalPages: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['cambiar-pagina', 'update:search-query'])

function formatRating(rating) {
  return Number(rating || 0).toFixed(1)
}

function paginaAnterior() {
  if (props.currentPage > 0) {
    emit('cambiar-pagina', props.currentPage - 1)
  }
}

function paginaSiguiente() {
  if (props.currentPage < props.totalPages - 1) {
    emit('cambiar-pagina', props.currentPage + 1)
  }
}

function irAPagina(pagina) {
  emit('cambiar-pagina', pagina)
}

function mostrarPaginas() {
  const paginas = []
  const total = props.totalPages
  const actual = props.currentPage
  const maxVisibles = 5

  let inicio = Math.max(0, actual - Math.floor(maxVisibles / 2))
  let fin = Math.min(total, inicio + maxVisibles)

  if (fin - inicio < maxVisibles) {
    inicio = Math.max(0, fin - maxVisibles)
  }

  for (let i = inicio; i < fin; i++) {
    paginas.push(i)
  }
  return paginas
}
</script>

<template>
  <div class="container my-4">
    <div class="search-bar mb-4">
      <div class="input-group shadow-sm rounded-4 overflow-hidden border border-light">
        <span class="input-group-text bg-white border-0 ps-3">
          <i class="bi bi-search text-primary fs-5"></i>
        </span>
        <input
          type="text"
          class="form-control border-0 py-2 fs-5"
          placeholder="Buscar por nombre, categoría o descripción..."
          :value="searchQuery"
          @input="$emit('update:search-query', $event.target.value)"
          aria-label="Buscar comercios"
        />
        <button
          v-if="searchQuery"
          class="btn btn-link text-decoration-none px-3"
          @click="$emit('update:search-query', '')"
          aria-label="Limpiar búsqueda"
        >
          <i class="bi bi-x-lg text-muted"></i>
        </button>
      </div>
    </div>

    <div v-if="searchQuery && totalResultados !== undefined" class="mb-3">
      <p class="text-muted mb-0 fw-medium">
        <i class="bi bi-funnel-fill me-1"></i>
        {{ totalResultados }} {{ totalResultados === 1 ? 'resultado' : 'resultados' }}
      </p>
    </div>

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

    <nav v-if="totalPages > 1" class="mt-4" aria-label="Paginación de comercios">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage <= 0 }">
          <button class="page-link" @click="paginaAnterior" :disabled="currentPage <= 0">
            <i class="bi bi-chevron-left"></i>
          </button>
        </li>
        <li
          v-for="pagina in mostrarPaginas()"
          :key="pagina"
          class="page-item"
          :class="{ active: pagina === currentPage }"
        >
          <button class="page-link" @click="irAPagina(pagina)">{{ pagina + 1 }}</button>
        </li>
        <li class="page-item" :class="{ disabled: currentPage >= totalPages - 1 }">
          <button class="page-link" @click="paginaSiguiente" :disabled="currentPage >= totalPages - 1">
            <i class="bi bi-chevron-right"></i>
          </button>
        </li>
      </ul>
    </nav>
  </div>
</template>

<style scoped>
#comercioGrid { transition: all 0.3s ease; }

.search-bar .input-group:focus-within {
  box-shadow: 0 0 0 3px rgba(58, 134, 255, 0.15);
  border-radius: 16px;
}

.search-bar .form-control:focus {
  box-shadow: none;
}

.search-bar .form-control::placeholder {
  color: #94a3b8;
}

.pagination .page-link {
  border-radius: 8px;
  margin: 0 2px;
  color: #3a86ff;
  border: 1px solid #e0e0e0;
  padding: 0.5rem 0.9rem;
  font-weight: 500;
  cursor: pointer;
}

.pagination .page-link:hover {
  background-color: #f0f4ff;
  border-color: #3a86ff;
}

.pagination .page-item.active .page-link {
  background-color: #3a86ff;
  border-color: #3a86ff;
  color: #fff;
}

.pagination .page-item.disabled .page-link {
  color: #adb5bd;
  pointer-events: none;
  background-color: #f8f9fa;
}

.pagination .page-item:first-child .page-link {
  border-top-left-radius: 8px;
  border-bottom-left-radius: 8px;
}

.pagination .page-item:last-child .page-link {
  border-top-right-radius: 8px;
  border-bottom-right-radius: 8px;
}
</style>
