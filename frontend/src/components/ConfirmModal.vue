<script setup>
import { useConfirm, setPromptValue } from '../utils/confirmService'

const { state, confirmAction } = useConfirm()

const variantBtnMap = {
  danger: 'btn-danger',
  primary: 'btn-primary',
  warning: 'btn-warning text-dark',
}
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="state.visible" class="confirm-backdrop" @click.self="confirmAction(false)">
        <div class="confirm-card" :class="{ 'prompt-mode': state.mode === 'prompt' }" role="dialog" aria-modal="true" aria-labelledby="confirm-title">
          <div class="confirm-icon">
            <i :class="'bi ' + state.icon + ' icon-' + state.variant"></i>
          </div>
          <h5 id="confirm-title" class="fw-bold mb-2">{{ state.title }}</h5>
          <p class="text-muted mb-4" style="white-space: pre-line;">{{ state.message }}</p>

          <div v-if="state.mode === 'prompt'" class="mb-4">
            <textarea
              class="form-control"
              :placeholder="state.promptPlaceholder"
              :value="state.promptValue"
              @input="setPromptValue($event.target.value)"
              rows="3"
              style="border-radius: 12px; resize: vertical;"
            ></textarea>
          </div>

          <div class="d-flex gap-2 justify-content-end">
            <button class="btn btn-light px-4 fw-semibold" @click="confirmAction(false)">{{ state.cancelText }}</button>
            <button
              class="btn px-4 fw-semibold"
              :class="variantBtnMap[state.variant] || 'btn-danger'"
              @click="confirmAction(true)"
              :disabled="state.mode === 'prompt' && !state.promptValue.trim()"
            >
              {{ state.confirmText }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.confirm-backdrop {
  position: fixed;
  inset: 0;
  z-index: 1060;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(4px);
}

.confirm-card {
  background: #fff;
  border-radius: 16px;
  padding: 2rem 1.75rem 1.5rem;
  max-width: 420px;
  width: 90%;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
  text-align: center;
}

.confirm-icon {
  margin-bottom: 1rem;
}

.confirm-icon i {
  font-size: 2.5rem;
}

.icon-danger {
  color: #dc3545;
}

.icon-primary {
  color: var(--db-primary, #0d6efd);
}

.icon-warning {
  color: #ffc107;
}

/* Transition */
.modal-enter-active {
  transition: all 0.2s ease-out;
}

.modal-leave-active {
  transition: all 0.15s ease-in;
}

.modal-enter-from {
  opacity: 0;
}

.modal-enter-from .confirm-card {
  transform: scale(0.92) translateY(12px);
}

.modal-leave-to {
  opacity: 0;
}

.modal-leave-to .confirm-card {
  transform: scale(0.95) translateY(8px);
}

.confirm-card {
  transition: transform 0.2s ease-out;
}
</style>
