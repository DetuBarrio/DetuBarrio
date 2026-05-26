import { createRouter, createWebHashHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ComercioView from '../views/ComercioView.vue'
import ComercioDetailView from '../views/ComercioDetailView.vue'
import AboutView from '../views/AboutView.vue'
import ContactoView from '../views/ContactoView.vue'
import LoginView from '../views/LoginView.vue'
import ForgotPasswordView from '../views/ForgotPasswordView.vue' // 🔐 IMPORTACIÓN AÑADIDA
import AdminView from '../views/AdminView.vue'
import AdminPendingCommercesView from '../views/AdminPendingCommercesView.vue'
import UsuarioDashboardView from '../views/UsuarioDashboardView.vue'
import ComercioDashboardView from '../views/ComercioDashboardView.vue'
import ResetPasswordView from '../views/ResetPasswordView.vue'
import FavoritosView from '../views/FavoritosView.vue' // 🌟 IMPORTACIÓN AÑADIDA
import { getAuth } from '../services/authService'

function routeForAuth(auth) {
  if (!auth?.token) {
    return { name: 'login' }
  }

  if (auth.rol === 'ADMIN') {
    return { name: 'admin' }
  }

  if (auth.rol === 'COMERCIO') {
    return { name: 'dashboard-comercio' }
  }

  if (auth.rol === 'USUARIO') {
    return { name: 'dashboard-usuario' }
  }

  return { name: 'home' }
}

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/comercios',
      name: 'comercios',
      component: ComercioView,
    },
    {
      path: '/comercios/:id',
      name: 'comercio-detalle',
      component: ComercioDetailView,
    },
    {
      path: '/favoritos', // 🌟 RUTA DE FAVORITOS AÑADIDA
      name: 'favoritos',
      component: FavoritosView,
    },
    {
      path: '/about',
      name: 'about',
      component: AboutView,
    },
    {
      path: '/contacto',
      name: 'contacto',
      component: ContactoView,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: {
        guestOnly: true,
      },
    },
    {
      path: '/forgot-password', // 🔐 RUTA DE RECUPERACIÓN AÑADIDA
      name: 'forgot-password',
      component: ForgotPasswordView,
      meta: {
        guestOnly: true, // Para que si ya está logueado, lo redirija a su sitio
      },
    },
    {
      path: '/reset-password', // 🔐 NUEVA RUTA: Establecer nueva contraseña
      name: 'reset-password',
      component: ResetPasswordView,
      meta: {
        guestOnly: true, // Solo accesible para usuarios sin sesión activa
      },
    },
    {
      path: '/mi-cuenta',
      name: 'account',
      redirect: () => routeForAuth(getAuth()),
    },
    {
      path: '/dashboard/usuario',
      name: 'dashboard-usuario',
      component: UsuarioDashboardView,
      meta: {
        requiredRole: 'USUARIO',
      },
    },
    {
      path: '/dashboard/comercio',
      name: 'dashboard-comercio',
      component: ComercioDashboardView,
      meta: {
        requiredRole: 'COMERCIO',
      },
    },
    {
      path: '/dashboard/disponibilidad',
      name: 'disponibilidad',
      component: () => import('../views/DisponibilidadView.vue')
    },
    {
      path: '/dashboard/configuracion',
      name: 'Configuracion',
      component: () => import('../views/ConfiguracionView.vue')
    },
    {
      path: '/dashboard/comercio/reservas',
      name: 'mis-reservas',
      component: () => import('../views/MisReservasView.vue')
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminView,
      meta: {
        requiresAdmin: true,
      },
    },
    {
      path: '/admin/solicitudes-comercios',
      name: 'admin-pending-commerces',
      component: AdminPendingCommercesView,
      meta: {
        requiresAdmin: true,
      },
    },
  ],
})

router.beforeEach((to) => {
  const auth = getAuth()

  if (to.meta.guestOnly && auth?.token) {
    return routeForAuth(auth)
  }

  if (to.meta.requiresAdmin) {
    if (!auth?.token) {
      return { name: 'login' }
    }

    if (auth.rol !== 'ADMIN') {
      return routeForAuth(auth)
    }
  }

  if (to.meta.requiredRole) {
    if (!auth?.token) {
      return { name: 'login' }
    }

    if (auth.rol !== to.meta.requiredRole) {
      return routeForAuth(auth)
    }
  }

  return true
})

export default router