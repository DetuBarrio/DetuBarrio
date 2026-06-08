import { reactive } from 'vue'

const state = reactive({
  visible: false,
  mode: 'confirm',
  title: '',
  message: '',
  confirmText: 'Sí, confirmar',
  cancelText: 'Cancelar',
  variant: 'danger',
  icon: 'bi-exclamation-triangle-fill',
  promptValue: '',
  promptPlaceholder: '',
  resolve: null,
})

export function showConfirm({ title, message, confirmText = 'Sí, confirmar', cancelText = 'Cancelar', variant = 'danger', icon = 'bi-exclamation-triangle-fill' }) {
  return new Promise((resolve) => {
    state.mode = 'confirm'
    state.visible = true
    state.title = title
    state.message = message
    state.confirmText = confirmText
    state.cancelText = cancelText
    state.variant = variant
    state.icon = icon
    state.promptValue = ''
    state.resolve = resolve
  })
}

export function showPrompt({ title, message, confirmText = 'Enviar', cancelText = 'Cancelar', placeholder = '', variant = 'primary' }) {
  return new Promise((resolve) => {
    state.mode = 'prompt'
    state.visible = true
    state.title = title
    state.message = message
    state.confirmText = confirmText
    state.cancelText = cancelText
    state.variant = variant
    state.icon = 'bi-pencil-fill'
    state.promptValue = ''
    state.promptPlaceholder = placeholder
    state.resolve = resolve
  })
}

export function setPromptValue(value) {
  state.promptValue = value
}

export function confirmAction(confirmed) {
  if (state.resolve) {
    if (state.mode === 'prompt' && confirmed) {
      state.resolve(state.promptValue.trim())
    } else {
      state.resolve(confirmed)
    }
    state.resolve = null
  }
  state.visible = false
}

export function useConfirm() {
  return { state, showConfirm, showPrompt, setPromptValue, confirmAction }
}
