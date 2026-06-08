# Arquitectura de DeTuBarrio

## 1. Visión General

DeTuBarrio sigue una **arquitectura cliente-servidor** con separación completa entre frontend y backend, comunicación mediante API REST, y autenticación stateless basada en JWT.

```
[Cliente Web (Navegador)]
         │
         │ HTTPS
         ▼
[Frontend SPA — Vue 3 + Vite]
         │
         │ Peticiones HTTP (JSON) → /api/*
         │ Proxy Vite en desarrollo
         ▼
[Backend API REST — Spring Boot 3.5]
         │
         │ JPA / Hibernate
         │ Flyway (migraciones)
         ▼
[Base de Datos — MySQL 8.x (Aiven/local)]
         │
         └── Cloudinary (almacenamiento de imágenes)
         └── SMTP Gmail (notificaciones email)
         └── OpenPDF (generación de documentos PDF)
```

### Principios arquitectónicos

- **Monorepo**: backend y frontend en un mismo repositorio para facilitar el desarrollo integrado.
- **API first**: el frontend consume exclusivamente la API REST; no hay lógica de negocio duplicada.
- **Stateless**: el backend no mantiene sesión; la autenticación se delega en tokens JWT.
- **Migraciones versionadas**: Flyway es la fuente de verdad del esquema de base de datos.
- **Configuración externalizada**: todas las credenciales y parámetros de entorno se inyectan mediante variables de entorno.

---

## 2. Backend (Spring Boot)

### 2.1 Estructura en capas

```
┌──────────────────────────────────────────────────┐
│                   Controller                      │  ← Endpoints REST
├──────────────────────────────────────────────────┤
│                    Service                        │  ← Lógica de negocio
├──────────────────────────────────────────────────┤
│                  Repository                       │  ← Acceso a datos (Spring Data JPA)
├──────────────────────────────────────────────────┤
│      Model (Entity)          DTO                  │  ← Modelo de dominio y transferencia
├──────────────────────────────────────────────────┤
│  Security (JWT Filter)   Config (CORS, OpenAPI)   │  ← Infraestructura
└──────────────────────────────────────────────────┘
```

### 2.2 Controladores (14)

| Controlador | Base path | Propósito |
|---|---|---|
| `HealthController` | `/api/health` | Health check del sistema |
| `AuthController` | `/api/auth` | Registro, login, perfil, cambio/recuperación de contraseña |
| `CategoriaController` | `/api/categorias` | CRUD de categorías |
| `ComercioController` | `/api/comercios` | CRUD de comercios, productos, imágenes |
| `ResenaController` | `/api/comercios/{id}/resenas` | Reseñas y valoraciones |
| `ComentarioController` | `/api/comentarios` | Comentarios sobre comercios |
| `ReservaController` | `/api/reservas` | Creación, consulta y cancelación de reservas |
| `DisponibilidadController` | `/api/disponibilidad` | Configuración de horarios por comercio |
| `FavoritoController` | `/api/favoritos` | Gestión de favoritos del usuario |
| `ContactoController` | `/api/contacto` | Formularios de contacto y colaboración |
| `DashboardController` | `/api/dashboard` | Datos de panel para usuario y comercio |
| `ClienteController` | `/api/clientes` | Listado de clientes por comercio |
| `AdminController` | `/api/admin` | Gestión de comercios (aprobar/rechazar) |
| `AdminContactoController` | `/api/admin/contacto` | Gestión de mensajes y colaboraciones |

### 2.3 Servicios (12)

| Servicio | Responsabilidad |
|---|---|
| `AuthService` | Registro, login, generación/validación de JWT |
| `UsuarioService` | Perfil, cambio de contraseña, recuperación de cuenta |
| `CategoriaService` | Listado y creación de categorías |
| `ComercioService` | CRUD de comercios, productos, imágenes, búsqueda |
| `ResenaService` | Creación y listado de reseñas |
| `ReservaService` | Gestión de reservas |
| `DisponibilidadService` | Configuración de franjas horarias |
| `FavoritoService` | Marcado/desmarcado de favoritos |
| `ContactoService` | Procesamiento de formularios de contacto |
| `AdminService` | Aprobación/rechazo de comercios y colaboraciones |
| `EmailService` | Envío de notificaciones por correo electrónico |
| `PdfService` | Generación de documentos PDF |

### 2.4 Modelo de datos (14 entidades)

| Entidad | Tabla | Propósito |
|---|---|---|
| `Usuario` | `usuarios` | Usuarios de la plataforma |
| `Comercio` | `comercios` | Comercios registrados |
| `Categoria` | `categoria` | Categorías de comercios |
| `Producto` | `productos` | Productos/servicios de un comercio |
| `Resena` | `resenas` | Reseñas y valoraciones |
| `Comentario` | `comentarios` | Comentarios genéricos |
| `Reserva` | `reservas` | Reservas de usuarios en comercios |
| `Disponibilidad` | `disponibilidades` | Franjas horarias disponibles |
| `Favorito` | `favoritos` | Relación usuario-comercio favorito |
| `MensajeContacto` | `mensaje_contacto` | Mensajes del formulario de contacto |
| `SolicitudColaboracion` | `solicitud_colaboracion` | Solicitudes de alta de comercios |
| `PasswordResetToken` | `password_reset_tokens` | Tokens para recuperación de contraseña |

### 2.5 Seguridad

```
Petición HTTP → JwtAuthenticationFilter → SecurityContextHolder
                      │
                      ├── Token válido   → Autenticación establecida
                      ├── Token inválido → 401 Unauthorized
                      └── Sin token      → Acceso público (si el endpoint lo permite)
```

- **Stateless**: no se utiliza `HttpSession`, cada petición lleva el token JWT.
- **JWT**: firmado con HMAC-SHA256, incluye `sub` (email), `rol` y `exp` (expiración).
- **BCrypt**: las contraseñas se almacenan hasheadas con BCrypt.
- **Roles**: `USUARIO`, `COMERCIO`, `ADMIN`. Los endpoints se protegen con `hasRole()` y `hasAuthority()`.
- **CORS**: configurable mediante `APP_CORS_ALLOWED_ORIGINS` (por defecto `*` en desarrollo).
- **Endpoints públicos**: `/api/auth/register`, `/api/auth/login`, `/api/auth/forgot-password`, `/api/auth/reset-password`, `/api/health`, `/api/categorias`, `/api/comercios/**` (GET), `/api/contacto/**`.
- **Endpoints protegidos**: `/api/auth/me`, `/api/reservas/**`, `/api/favoritos/**`, `/api/dashboard/**`.
- **Endpoints de administración**: `/api/admin/**` requieren rol `ADMIN`.

---

## 3. Frontend (Vue 3)

### 3.1 Estructura

```
src/
├── main.js                      # Punto de entrada
├── App.vue                      # Componente raíz
├── assets/                      # CSS, imágenes, recursos estáticos
├── components/                  # Componentes reutilizables
│   ├── ComercioCard.vue         # Tarjeta de comercio
│   ├── ComercioFilters.vue      # Filtros de búsqueda
│   ├── ComercioResults.vue      # Resultados de búsqueda
│   ├── AppBreadcrumbs.vue       # Migas de pan
│   ├── ConfirmModal.vue         # Modal de confirmación
│   ├── OpenedBadge.vue          # Indicador de horario
│   └── ToastContainer.vue       # Notificaciones toast
├── composables/                 # Lógica reactiva compartida
│   └── useComercioList.js       # Composable para listado de comercios
├── config/                      # Configuración de la aplicación
│   └── api.js                   # Función apiUrl() para construir URLs
├── router/                      # Enrutador Vue
│   └── index.js                 # Definición de rutas y guards
├── services/                    # Clientes HTTP (Axios)
│   ├── authService.js           # Autenticación JWT
│   ├── adminService.js          # Operaciones de administración
│   ├── comercioService.js       # Catálogo de comercios
│   ├── contactoService.js       # Formularios de contacto
│   ├── dashboardService.js      # Datos de dashboard
│   ├── disponibilidadService.js # Gestión de horarios
│   ├── reservaService.js        # Reservas
│   └── userService.js           # Perfil de usuario
├── utils/                       # Utilidades
│   ├── comercioHelpers.js       # Helpers para comercios
│   ├── confirmService.js        # Servicio de confirmación modal
│   └── toastService.js          # Servicio de notificaciones
└── views/                       # Vistas/páginas
    ├── HomeView.vue             # Página principal
    ├── ComercioView.vue         # Listado de comercios
    ├── ComercioDetailView.vue   # Detalle de comercio
    ├── LoginView.vue            # Login y registro
    ├── ContactoView.vue         # Contacto y colaboración
    ├── FavoritosView.vue        # Favoritos del usuario
    ├── AboutView.vue            # Acerca de
    ├── FaqView.vue              # Preguntas frecuentes
    ├── TerminosView.vue         # Términos y condiciones
    ├── PrivacidadView.vue       # Política de privacidad
    ├── AdminView.vue            # Panel de administración
    ├── AdminPendingCommercesView.vue  # Solicitudes de comercios
    └── dashboard/               # Paneles role-based
        ├── UsuarioDashboardLayout.vue
        ├── UsuarioDashboardHome.vue
        ├── UsuarioPerfilView.vue
        ├── UsuarioAjustesView.vue
        ├── ComercioDashboardLayout.vue
        ├── ComercioDashboardHome.vue
        ├── MisReservasView.vue
        ├── DisponibilidadView.vue
        ├── ConfiguracionView.vue
        ├── ClientesView.vue
        ├── ReservasUsuario.vue
        ├── ComercioDashboardView.vue
        └── UsuarioDashboardView.vue
```

### 3.2 Enrutamiento y Guards

- **Hash-based routing** (`createWebHashHistory`) para evitar problemas con URLs en despliegue.
- **Guard global** (`router.beforeEach`):
  - Redirige a usuarios autenticados lejos de páginas de login/registro (`guestOnly`).
  - Verifica el rol requerido para rutas protegidas (`requiredRole`).
  - Bloquea rutas de administración si el usuario no es `ADMIN` (`requiresAdmin`).
  - Redirige a la ruta correspondiente según el rol del usuario.

### 3.3 Flujo de autenticación

1. El usuario inicia sesión mediante `POST /api/auth/login`.
2. El backend devuelve un token JWT que se almacena en `localStorage` bajo la clave `detubarrio_auth`.
3. El interceptor de Axios añade automáticamente la cabecera `Authorization: Bearer <token>` a todas las peticiones autenticadas.
4. El guard de rutas verifica la existencia y validez del token antes de permitir el acceso.
5. Al cerrar sesión, se elimina el token de `localStorage`.

---

## 4. Base de Datos

### 4.1 Gestión de migraciones

- **Flyway** es la herramienta de migración.
- Las migraciones se encuentran en `backend/src/main/resources/db/migration/`.
- Estrategia: `spring.jpa.hibernate.ddl-auto=validate` — Hibernate solo valida que las entidades coincidan con el esquema existente.
- Para añadir cambios al esquema, crear nuevos archivos versionados (`V3__...sql`, `V4__...sql`, etc.).
- No modificar migraciones ya aplicadas en entornos compartidos.

### 4.2 Migraciones existentes

| Archivo | Contenido |
|---|---|
| `V1__all_in_one.sql` | Creación de todas las tablas del sistema |
| `V2__seed_data.sql` | Datos de prueba (categorías, comercios, productos, usuarios) |

### 4.3 Testing

- En el perfil de test se utiliza **H2 en modo MySQL** (`MODE=MySQL`).
- `ddl-auto=create-drop` — el esquema se crea a partir de las entidades y se elimina al finalizar.
- No se aplican migraciones Flyway en tests.

---

## 5. Configuración de Entorno

El backend carga las variables de entorno desde `backend/.env` mediante:

```properties
spring.config.import=optional:file:.env[.properties]
```

### Variables críticas

| Variable | Descripción | Obligatoria |
|---|---|---|
| `DB_URL` | URL JDBC de conexión a MySQL | Sí |
| `DB_USER` | Usuario de base de datos | Sí |
| `DB_PASSWORD` | Contraseña de base de datos | Sí |
| `APP_JWT_SECRET` | Clave secreta para firmar JWT | Sí |
| `APP_JWT_EXPIRATION` | Tiempo de vida del token (segundos) | Sí |
| `APP_CORS_ALLOWED_ORIGINS` | Orígenes permitidos para CORS | No (default *) |
| `CLOUDINARY_CLOUD_NAME` | Cloud name de Cloudinary | No |
| `CLOUDINARY_API_KEY` | API Key de Cloudinary | No |
| `CLOUDINARY_API_SECRET` | API Secret de Cloudinary | No |
| `EMAIL_USER` | Usuario SMTP Gmail | No |
| `EMAIL_PASSWORD` | Contraseña de aplicación Gmail | No |

> El archivo `.env` con valores reales **no debe subirse al repositorio**. Solo se comparte `.env.example` como plantilla.

---

## 6. Despliegue

### Backend (Render)
- **Contenedor Docker**: multi-stage build (Maven → JRE).
- **Puerto**: 8080 (expuesto en el contenedor).
- **Health check**: `GET /api/health`.
- **Plan gratuito**: el servicio se duerme tras 15 min de inactividad.

### Frontend (Vercel)
- **Build**: `vite build` → directorio `dist`.
- **Proxy**: no aplica en producción; el frontend llama directamente a la URL del backend mediante `VITE_API_URL`.
- **Despliegues automáticos**: cada push a la rama configurada dispara un nuevo deploy.

### Infraestructura cloud
- **Base de datos**: Aiven MySQL 8.x.
- **Imágenes**: Cloudinary.
- **Email**: SMTP Gmail con contraseña de aplicación.

---

## 7. Decisiones Técnicas

| Decisión | Alternativa | Motivo |
|---|---|---|
| **Monorepo** | Repos separados | Desarrollo integrado más ágil para un equipo pequeño |
| **JWT stateless** | Sesiones HTTP | Escalabilidad horizontal sin estado compartido |
| **Flyway** | Hibernate DDL auto | Control de versiones del esquema explícito y seguro |
| **Hash routing** | History routing | Compatibilidad con despliegues estáticos (Vercel) |
| **Cloudinary** | Almacenamiento local | Escalabilidad, rendimiento y backup de imágenes |
| **Bootstrap 5** | Tailwind, Material UI | Curva de aprendizaje baja, ecosistema maduro |
| **Axios** | Fetch nativo | Interceptores, manejo de errores, compatibilidad |
| **OpenPDF** | iText, Apache PDFBox | Ligero, sin restricciones de licencia comercial |

---

## 8. Riesgos y Recomendaciones

### Riesgos actuales

| Riesgo | Impacto | Mitigación |
|---|---|---|
| CORS abierto (`*`) en producción | Seguridad | Configurar `APP_CORS_ALLOWED_ORIGINS` con el origen real |
| Sin tests automatizados frontend | Calidad | Añadir Vitest para tests unitarios y Cypress para E2E |
| Sin pipeline CI/CD | Integración continua | Configurar GitHub Actions para build, test y deploy |
| Sin rate limiting | Abuso de API | Implementar filtro de rate limiting en Spring Security |
| Sin logging estructurado | Depuración | Configurar Logback con formato JSON para producción |


