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
import ResetPasswordView from '../views/ResetPasswordView.vue'
import FavoritosView from '../views/FavoritosView.vue' // 🌟 IMPORTACIÓN AÑADIDA
import UsuarioDashboardLayout from '../views/dashboard/UsuarioDashboardLayout.vue'
import UsuarioDashboardHome from '../views/dashboard/UsuarioDashboardHome.vue'
import UsuarioPerfilView from '../views/dashboard/UsuarioPerfilView.vue'
import UsuarioAjustesView from '../views/dashboard/UsuarioAjustesView.vue'
import ComercioDashboardLayout from '../views/dashboard/ComercioDashboardLayout.vue'
import ComercioDashboardHome from '../views/dashboard/ComercioDashboardHome.vue'
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
      meta: {
        breadcrumb: 'Inicio',
      },
    },
    {
      path: '/comercios',
      name: 'comercios',
      component: ComercioView,
      meta: {
        breadcrumb: 'Comercios',
      },
    },
    {
      path: '/comercios/:id',
      name: 'comercio-detalle',
      component: ComercioDetailView,
      meta: {
        breadcrumb: 'comercio-detalle',
      },
    },
    {
      path: '/favoritos', // 🌟 RUTA DE FAVORITOS AÑADIDA
      name: 'favoritos',
      component: FavoritosView,
      meta: {
        breadcrumb: 'Favoritos',
      },
    },
    {
      path: '/about',
      name: 'about',
      component: AboutView,
      meta: {
        breadcrumb: '¿Qué es?',
      },
    },
    {
      path: '/contacto',
      name: 'contacto',
      component: ContactoView,
      meta: {
        breadcrumb: 'Contacto',
      },
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: {
        guestOnly: true,
        breadcrumb: 'Iniciar sesión',
      },
    },
    {
      path: '/forgot-password', // 🔐 RUTA DE RECUPERACIÓN AÑADIDA
      name: 'forgot-password',
      component: ForgotPasswordView,
      meta: {
        guestOnly: true, // Para que si ya está logueado, lo redirija a su sitio
        breadcrumb: 'Recuperar acceso',
      },
    },
    {
      path: '/reset-password', // 🔐 NUEVA RUTA: Establecer nueva contraseña
      name: 'reset-password',
      component: ResetPasswordView,
      meta: {
        guestOnly: true, // Solo accesible para usuarios sin sesión activa
        breadcrumb: 'Nueva contraseña',
      },
    },
    {
      path: '/mi-cuenta',
      name: 'account',
      redirect: () => routeForAuth(getAuth()),
    },
    {
      path: '/dashboard/usuario',
      component: UsuarioDashboardLayout,
      meta: {
        requiredRole: 'USUARIO',
        breadcrumb: 'Mi cuenta',
      },
      children: [
        {
          path: '',
          name: 'dashboard-usuario',
          component: UsuarioDashboardHome,
          meta: {
            breadcrumb: 'Panel general',
          },
        },
        {
          path: 'reservas',
          name: 'dashboard-usuario-reservas',
          component: () => import('../views/ReservasUsuario.vue'),
          meta: {
            breadcrumb: 'Mis reservas',
          },
        },
        {
          path: 'perfil',
          name: 'dashboard-usuario-perfil',
          component: UsuarioPerfilView,
          meta: {
            breadcrumb: 'Mi perfil',
          },
        },
        {
          path: 'ajustes',
          name: 'dashboard-usuario-ajustes',
          component: UsuarioAjustesView,
          meta: {
            breadcrumb: 'Ajustes',
          },
        },
      ],
    },
    {
      path: '/dashboard/comercio',
      component: ComercioDashboardLayout,
      meta: {
        requiredRole: 'COMERCIO',
        breadcrumb: 'Mi comercio',
      },
      children: [
        {
          path: '',
          name: 'dashboard-comercio',
          component: ComercioDashboardHome,
          meta: {
            breadcrumb: 'Panel general',
          },
        },
        {
          path: 'reservas',
          name: 'dashboard-comercio-reservas',
          component: () => import('../views/MisReservasView.vue'),
          meta: {
            breadcrumb: 'Reservas',
          },
        },
        {
          path: 'disponibilidad',
          name: 'dashboard-comercio-disponibilidad',
          component: () => import('../views/DisponibilidadView.vue'),
          meta: {
            breadcrumb: 'Horarios',
          },
        },
        {
          path: 'configuracion',
          name: 'dashboard-comercio-configuracion',
          component: () => import('../views/ConfiguracionView.vue'),
          meta: {
            breadcrumb: 'Configuración',
          },
        },
        // 🌟 NUEVA RUTA DE CLIENTES DESBLOQUEADA AQUÍ:
        {
          path: 'clientes',
          name: 'dashboard-comercio-clientes',
          component: () => import('../views/ClientesView.vue'),
          meta: {
            breadcrumb: 'Clientes',
          },
        },
      ],
    },
    {
      path: '/dashboard/disponibilidad',
      redirect: { name: 'dashboard-comercio-disponibilidad' },
    },
    {
      path: '/dashboard/configuracion',
      redirect: { name: 'dashboard-comercio-configuracion' },
    },
    {
      path: '/dashboard/comercio/reservas',
      redirect: { name: 'dashboard-comercio-reservas' },
    },
    // Extra por si entran a la antigua URL sin barra
    {
      path: '/dashboard/comercio/clientes',
      redirect: { name: 'dashboard-comercio-clientes' },
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminView,
      meta: {
        requiresAdmin: true,
        breadcrumb: 'Admin',
      },
    },
    {
      path: '/admin/solicitudes-comercios',
      name: 'admin-pending-commerces',
      component: AdminPendingCommercesView,
      meta: {
        requiresAdmin: true,
        breadcrumb: 'Solicitudes',
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