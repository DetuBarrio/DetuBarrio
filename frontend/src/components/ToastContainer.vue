<script setup>
import { useToast } from '../utils/toastService'

const { toasts, dismissToast } = useToast()

const iconMap = {
  success: 'bi-check-circle-fill',
  error: 'bi-x-circle-fill',
  warning: 'bi-exclamation-triangle-fill',
  info: 'bi-info-circle-fill',
}

const bgMap = {
  success: 'bg-success',
  error: 'bg-danger',
  warning: 'bg-warning',
  info: 'bg-primary',
}
</script>

<template>
  <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 9999;">
    <div
      v-for="toast in toasts"
      :key="toast.id"
      class="toast show align-items-center text-bg-light border-0 mb-2 shadow-lg"
      role="alert"
      aria-live="assertive"
      aria-atomic="true"
      style="min-width: 280px; max-width: 420px; border-radius: 12px;"
    >
      <div class="d-flex">
        <div
          class="toast-body d-flex align-items-center gap-2 fw-semibold"
          :class="bgMap[toast.type] + ' text-white rounded-start'"
          style="flex: 1; border-radius: 12px 0 0 12px;"
        >
          <i :class="'bi ' + iconMap[toast.type] + ' fs-5'"></i>
          <span>{{ toast.message }}</span>
        </div>
        <button
          type="button"
          class="btn-close btn-close-white me-2 mt-2"
          aria-label="Cerrar"
          @click="dismissToast(toast.id)"
        ></button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.toast {
  animation: toast-slide-in 0.3s ease-out;
}

@keyframes toast-slide-in {
  from {
    opacity: 0;
    transform: translateX(100%);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>
