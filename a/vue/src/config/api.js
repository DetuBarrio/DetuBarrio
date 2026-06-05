export const API_URL = import.meta.env.VITE_API_URL.replace(/\/$/, '')

export function apiUrl(path = '') {
  if (!path) {
    return API_URL
  }

  if (/^https?:\/\//i.test(path) || path.startsWith('data:')) {
    return path
  }

  return path.startsWith('/') ? `${API_URL}${path}` : `${API_URL}/${path}`
}
