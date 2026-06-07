<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { fetchComercioById } from '../services/comercioService'

const route = useRoute()
const comercioDetalleNombre = ref('')

async function loadCommerceName() {
  if (route.name !== 'comercio-detalle') {
    comercioDetalleNombre.value = ''
    return
  }

  const comercioId = Number(route.params.id)
  if (!Number.isFinite(comercioId) || comercioId <= 0) {
    comercioDetalleNombre.value = ''
    return
  }

  try {
    const comercio = await fetchComercioById(comercioId)
    comercioDetalleNombre.value = comercio?.nombreComercio || comercio?.nombre || `Comercio ${comercioId}`
  } catch {
    comercioDetalleNombre.value = `Comercio ${comercioId}`
  }
}

const breadcrumbs = computed(() => {
  if (route.name === 'home') {
    return [
      { label: 'Inicio', target: { name: 'home' }, active: true },
    ]
  }

  const items = []

  items.push(
    { label: 'Inicio', target: { name: 'home' }, active: false },
  )

  if (route.path.startsWith('/admin') && route.name !== 'admin') {
    items.push(
      { label: 'Admin', target: { name: 'admin' }, active: false },
    )
  }

  if (route.name === 'comercio-detalle') {
    items.push(
      { label: 'Comercios', target: { name: 'comercios' }, active: false },
    )
  }

  const matched = route.matched
    .map((record, index, records) => {
      const label = typeof record.meta?.breadcrumb === 'function'
        ? record.meta.breadcrumb(route, comercioDetalleNombre.value)
        : record.meta?.breadcrumb === 'comercio-detalle'
          ? comercioDetalleNombre.value
          : record.meta?.breadcrumb
      if (!label) {
        return null
      }

      const isLast = index === records.length - 1
      const target = record.name
        ? { name: record.name, params: route.params, query: route.query }
        : record.path

      return {
        label,
        target,
        active: isLast,
      }
    })
    .filter(Boolean)

  if (route.name === 'comercio-detalle' && matched.length) {
    matched[matched.length - 1].active = true
  }

  return [...items, ...matched]
})

watch(
  () => [route.name, route.params.id],
  () => {
    loadCommerceName()
  },
  { immediate: true },
)

onMounted(() => {
  loadCommerceName()
})
</script>

<template>
  <nav v-if="breadcrumbs.length" class="app-breadcrumbs" aria-label="breadcrumb">
    <div class="container-fluid container-xl px-0">
      <ol class="breadcrumb mb-0 small align-items-center">
        <li
          v-for="(crumb, index) in breadcrumbs"
          :key="`${crumb.label}-${index}`"
          class="breadcrumb-item"
          :class="{ active: crumb.active }"
          :aria-current="crumb.active ? 'page' : null"
        >
          <RouterLink v-if="!crumb.active" :to="crumb.target" class="breadcrumb-link">
            {{ crumb.label }}
          </RouterLink>
          <span v-else>{{ crumb.label }}</span>
        </li>
      </ol>
    </div>
  </nav>
</template>

<style scoped>
.app-breadcrumbs {
  padding: 0.85rem 0 0.25rem;
}

.breadcrumb {
  background: #f8fbff;
  border: 1px solid rgba(15, 23, 42, 0.06);
  border-radius: 999px;
  padding: 0.65rem 1rem;
}

.breadcrumb-link {
  color: var(--db-secondary);
  text-decoration: none;
  font-weight: 600;
}

.breadcrumb-link:hover,
.breadcrumb-link:focus-visible {
  color: var(--db-primary);
  text-decoration: underline;
}

.breadcrumb-item.active {
  color: var(--db-primary);
  font-weight: 700;
}

.breadcrumb-item + .breadcrumb-item::before {
  color: #94a3b8;
}
</style>