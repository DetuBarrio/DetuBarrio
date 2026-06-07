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

  const currentPage = ref(0)
  const totalPages = ref(0)
  const totalElements = ref(0)
  const pageSize = ref(8)

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
    return comercios.value.filter((comercio) => {
      return cumpleRangoValoracion(comercio) && cumpleHorario(comercio) && cumpleBusqueda(comercio)
    })
  })

  const totalComercios = computed(() => totalElements.value)
  const totalResultados = computed(() => comerciosFiltrados.value.length)

  function limpiarFiltros() {
    categoriaSeleccionada.value = ''
    valoracionSeleccionada.value = ''
    horarioSeleccionado.value = ''
    searchQuery.value = ''
    currentPage.value = 0
    cargarComercios()
  }

  function cambiarPagina(pagina) {
    if (pagina < 0 || pagina >= totalPages.value) return
    currentPage.value = pagina
    cargarComercios()
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  function actualizarHoraActual() {
    horaActual.value = new Date()
  }

  async function cargarComercios() {
    isLoading.value = true
    errorMessage.value = ''
    try {
      const params = {
        page: currentPage.value,
        size: pageSize.value,
      }
      if (categoriaSeleccionada.value) {
        params.categoriaId = categoriaSeleccionada.value
      }
      const data = await fetchComercios(params)
      comercios.value = (data.content || []).map((i) => mapComercio(i, comercioImagesByName, DEFAULT_COMERCIO_IMAGE))
      totalPages.value = data.totalPages || 0
      totalElements.value = data.totalElements || 0
      currentPage.value = data.number || 0
    } catch (e) {
      console.error(e)
      errorMessage.value = 'No se pudieron cargar los comercios.'
      comercios.value = []
    } finally {
      isLoading.value = false
    }
  }

  watch(categoriaSeleccionada, () => {
    currentPage.value = 0
    cargarComercios()
  })

  watch(
    () => route.query.q,
    (value) => { searchQuery.value = typeof value === 'string' ? value : '' },
    { immediate: true },
  )

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

  watch(() => route.query.categoria, () => {
    aplicarCategoriaDeRuta()
  })

  onMounted(async () => {
    try {
      const cat = await fetchCategorias()
      categorias.value = cat
      aplicarCategoriaDeRuta()
      await cargarComercios()
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
    currentPage, totalPages, pageSize, cambiarPagina,
  }
}
