import { reactive } from 'vue'

const toasts = reactive([])
let nextId = 0

export function showToast(message, type = 'success', duration = 6000) {
  const id = ++nextId
  toasts.push({ id, message, type })
  setTimeout(() => {
    const index = toasts.findIndex(t => t.id === id)
    if (index !== -1) toasts.splice(index, 1)
  }, duration)
}

export function dismissToast(id) {
  const index = toasts.findIndex(t => t.id === id)
  if (index !== -1) toasts.splice(index, 1)
}

export function useToast() {
  return { toasts, showToast, dismissToast }
}
