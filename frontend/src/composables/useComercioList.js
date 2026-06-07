import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { fetchCategorias, fetchComercios } from '../services/comercioService'
import {
  valoracionOpciones,
  horarioOpciones,
  mapComercio,
} from '../utils/comercioHelpers'

export function useComercioList() {
  const comercios = ref([])
  const categorias = ref([])
  const categoriaSeleccionada = ref('')
  const valoracionSeleccionada = ref('')
  const horarioSeleccionado = ref('')
  const searchQuery = ref('')
  const horaActual = ref(new Date())
  const isLoading = ref(true)
  const errorMessage = ref('')
  let intervaloHoraActual = null
  const route = useRoute()

  const comercioImageModules = import.meta.glob('../assets/images/*.{png,jpg,jpeg,webp,svg,gif}', {
    eager: true,
    import: 'default',
  })

  const comercioImagesByName = Object.fromEntries(
    Object.entries(comercioImageModules).map(([path, url]) => [path.split('/').pop(), url]),
  )

  const DEFAULT_COMERCIO_IMAGE = comercioImagesByName.logo_og || comercioImagesByName['logo_og.png'] || '/images/logo_og.png'

  const formatRating = (rating) => {
    if (!rating) return "0.0";
    return Number(rating).toFixed(1);
  };

  const isComercioOpen = (comercio, hora) => {
    return true; 
  };

  function getCategoriaNombreById(categoriaId) {
    if (!categoriaId) return ''
    const catFound = categorias.value.find((c) => String(c.id || c.idCategoria) === String(categoriaId))
    return catFound ? (catFound.nombreCategoria || catFound.nombre || '') : ''
  }

  function cumpleRangoValoracion(comercio) {
      if (valoracionSeleccionada.value === '' || valoracionSeleccionada.value === null) {
        return true
      }
      const valorCorteSeleccionado = Number(valoracionSeleccionada.value)
      const valorComercio = Number(comercio.media || comercio.puntuacionMedia || 0)
      return valorComercio >= valorCorteSeleccionado
  }

  function cumpleHorario(comercio) {
    if (!horarioSeleccionado.value) return true
    const abiertoAhora = isComercioOpen(comercio, horaActual.value)
    return horarioSeleccionado.value === 'abierto' ? abiertoAhora : !abiertoAhora
  }

  function cumpleBusqueda(comercio) {
    const query = searchQuery.value.trim().toLowerCase()
    if (!query) return true
    const textoBuscable = [
      comercio.nombreComercio || comercio.nombre,
      comercio.descripcion,
      comercio.categoria,
      comercio.horario,
      comercio.diasApertura,
    ]
      .filter(Boolean)
      .join(' ')
      .toLowerCase()
    return textoBuscable.includes(query)
  }

  const comerciosFiltrados = computed(() => {
    const catNombre = getCategoriaNombreById(categoriaSeleccionada.value)
    return comercios.value.filter((comercio) => {
      const cumpleCat = !categoriaSeleccionada.value || 
                       (comercio.categoria?.toLowerCase() === catNombre.toLowerCase()) || 
                       (String(comercio.categoriaId || comercio.idCategoria) === String(categoriaSeleccionada.value))
      return cumpleCat && cumpleRangoValoracion(comercio) && cumpleHorario(comercio) && cumpleBusqueda(comercio)
    })
  })

  const totalComercios = computed(() => comercios.value.length)
  const totalResultados = computed(() => comerciosFiltrados.value.length)

  function limpiarFiltros() {
    categoriaSeleccionada.value = ''
    valoracionSeleccionada.value = ''
    horarioSeleccionado.value = ''
    searchQuery.value = ''
  }

  function actualizarHoraActual() {
    horaActual.value = new Date()
  }

  // Escucha los cambios del buscador de texto (?q=...) sin los puntos suspensivos molestos
  watch(
    () => route.query.q,
    (value) => { searchQuery.value = typeof value === 'string' ? value : '' },
    { immediate: true },
  )

  // OYENTE INTELIGENTE: Sincroniza la categoría pasada por URL (?categoria=Salud) con los desplegables
  const aplicarCategoriaDeRuta = () => {
    const catUrl = route.query.categoria
    if (catUrl && categorias.value.length > 0) {
      const encontrada = categorias.value.find(
        c => (c.nombreCategoria || c.nombre || '').toLowerCase() === String(catUrl).toLowerCase()
      )
      if (encontrada) {
        categoriaSeleccionada.value = encontrada.id || encontrada.idCategoria
      }
    }
  }

  // Vigila si la URL cambia dinámicamente mientras el usuario navega
  watch(() => route.query.categoria, () => {
    aplicarCategoriaDeRuta()
  })

  onMounted(async () => {
    try {
      const [cat, lista] = await Promise.all([fetchCategorias(), fetchComercios()])
      categorias.value = cat
      comercios.value = lista.map((i) => mapComercio(i, comercioImagesByName, DEFAULT_COMERCIO_IMAGE))
      
      // Una vez cargadas las categorías desde la BD, comprobamos si venimos rebotados de la Home
      aplicarCategoriaDeRuta()
    } catch (e) {
      console.error(e)
      errorMessage.value = 'No se pudieron cargar los comercios.'
    } finally {
      isLoading.value = false
    }
    actualizarHoraActual()
    intervaloHoraActual = window.setInterval(actualizarHoraActual, 60000)
  })

  onUnmounted(() => { if (intervaloHoraActual) window.clearInterval(intervaloHoraActual) })

  return {
    comercios, categorias, categoriaSeleccionada, valoracionSeleccionada, horarioSeleccionado,
    searchQuery, horaActual, isLoading, errorMessage, valoracionOpciones, horarioOpciones,
    comerciosFiltrados, totalComercios, totalResultados, limpiarFiltros, formatRating, isComercioOpen,
  }
}