# DeTuBarrio

**Plataforma digital para la promoción y gestión de comercios de barrio.**

DeTuBarrio es una aplicación web full-stack que conecta a los usuarios con los comercios locales de su barrio. Permite descubrir negocios de proximidad, consultar sus productos y servicios, realizar reservas, gestionar la disponibilidad horaria, y administrar todo el ecosistema desde paneles de control role-based.

Proyecto TFG del ciclo formativo de **2DAW (Desarrollo de Aplicaciones Web)**.

---

## Stack Tecnológico

### Backend
| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 3.5.11 |
| Spring Security | 6.x (JWT stateless) |
| Spring Data JPA / Hibernate | 6.x |
| Flyway | 10.x |
| MySQL (producción) / H2 (tests) | — |
| Swagger / OpenAPI (SpringDoc) | 2.8.6 |
| Cloudinary | 1.38.0 |
| OpenPDF | 2.0.3 |
| Lombok | — |

### Frontend
| Tecnología | Versión |
|---|---|
| Vue 3 (Composition API) | 3.5.32 |
| Vite | 8.0.8 |
| Vue Router | 5.0.4 |
| Bootstrap 5 | 5.3.8 |
| Axios | 1.16.0 |

---

## Funcionalidades Principales

- **Catálogo de comercios**: navegación por categorías, búsqueda y filtrado.
- **Ficha de comercio**: detalle con productos, reseñas, horarios y localización.
- **Autenticación JWT**: registro, inicio de sesión, recuperación de contraseña, perfil role-based.
- **Panel de usuario**: gestión de reservas, perfil, ajustes y favoritos.
- **Panel de comercio**: gestión de reservas, disponibilidad horaria, productos, clientes y configuración.
- **Panel de administración**: aprobación/rechazo de comercios, gestión de solicitudes de colaboración y mensajes de contacto.
- **Sistema de reseñas y comentarios**: valoraciones con puntuación numérica.
- **Favoritos**: marcado de comercios como favoritos.
- **Reservas online**: creación, consulta y cancelación de reservas.
- **Gestión de disponibilidad**: configuración de franjas horarias por comercio.
- **Subida de imágenes**: integración con Cloudinary para almacenamiento de imágenes.
- **Generación de PDF**: OpenPDF para documentos (facturas/comprobantes).
- **Notificaciones por email**: envío de correos SMTP para contacto, colaboración y recuperación de contraseña.
- **API documentada**: Swagger UI interactiva en `/swagger-ui.html`.
- **Roles de usuario**: `USUARIO`, `COMERCIO`, `ADMIN` con autorización granular.

---

## Estructura del Repositorio

```
DetuBarrio/
├── backend/                        # API REST Spring Boot
│   ├── src/
│   │   ├── main/java/detubarrio/rest/
│   │   │   ├── config/             # Seguridad, CORS, OpenAPI
│   │   │   ├── controller/         # 14 controladores REST
│   │   │   ├── dto/                # 32 DTOs de entrada/salida
│   │   │   ├── exception/          # Manejador global de excepciones
│   │   │   ├── model/              # 14 entidades JPA
│   │   │   ├── repository/         # 11 repositorios Spring Data
│   │   │   ├── security/           # Filtro JWT + utilidades
│   │   │   └── service/            # 12 servicios con lógica de negocio
│   │   ├── main/resources/
│   │   │   ├── application.properties
│   │   │   └── db/migration/       # Migraciones Flyway (V1, V2)
│   │   └── test/                   # Tests de integración
│   ├── dockerfile                  # Multi-stage build Docker
│   ├── .env.example
│   └── pom.xml
│
├── frontend/                       # SPA Vue 3
│   ├── src/
│   │   ├── assets/                 # CSS, imágenes, logo
│   │   ├── components/             # Componentes reutilizables
│   │   ├── composables/            # Lógica reactiva compartida
│   │   ├── config/                 # Configuración (api.js)
│   │   ├── router/                 # Definición de rutas y guards
│   │   ├── services/               # 8 servicios HTTP (Axios)
│   │   ├── utils/                  # Utilidades (toast, helpers)
│   │   └── views/                  # 25+ vistas organizadas por sección
│   ├── vercel.json
│   ├── vite.config.js
│   └── package.json
│
├── ARQUITECTURA.md                 # Documentación técnica
├── GUIA_DESPLIEGUE.md              # Guía de despliegue en producción
└── MANUAL_USO.md                   # Manual funcional por roles
```

---

## Requisitos Previos

- **Java 21** (JDK)
- **Maven Wrapper** (incluido en el repositorio)
- **Node.js 20+** (^20.19.0 || >=22.12.0)
- **MySQL** 8.x accesible (local o remoto, ej. Aiven)
- **Cuenta en Cloudinary** (para subida de imágenes)

---

## Configuración y Arranque Local

### 1. Backend (Spring Boot)

```powershell
cd backend
Copy-Item .env.example .env
```

Editar el archivo `.env` con los valores reales de conexión:

| Variable | Descripción |
|---|---|
| `DB_URL` | URL de conexión MySQL (formato JDBC) |
| `DB_USER` | Usuario de base de datos |
| `DB_PASSWORD` | Contraseña de base de datos |
| `APP_JWT_SECRET` | Clave secreta para firmar tokens JWT |
| `APP_JWT_EXPIRATION` | Tiempo de expiración del token (segundos) |
| `APP_CORS_ALLOWED_ORIGINS` | Orígenes permitidos para CORS |
| `CLOUDINARY_CLOUD_NAME` | Cloud name de Cloudinary |
| `CLOUDINARY_API_KEY` | API Key de Cloudinary |
| `CLOUDINARY_API_SECRET` | API Secret de Cloudinary |

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

La API estará disponible en: `http://localhost:8080`

Documentación interactiva:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

### 2. Frontend (Vue 3)

```powershell
cd frontend
npm install
npm run dev
```

La aplicación estará disponible en: `http://localhost:5173`

El frontend utiliza un proxy de Vite que redirige las peticiones `/api/*` hacia `http://localhost:8080`, por lo que no es necesario configurar CORS en desarrollo.

---

## Endpoints Principales de la API

### Salud del sistema
| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/api/health` | Público |

### Autenticación
| Método | Endpoint | Acceso |
|---|---|---|
| POST | `/api/auth/register` | Público |
| POST | `/api/auth/login` | Público |
| GET | `/api/auth/me` | Autenticado |
| PUT | `/api/auth/me` | Autenticado |
| DELETE | `/api/auth/me` | Autenticado |
| POST | `/api/auth/forgot-password` | Público |
| POST | `/api/auth/reset-password` | Público |
| POST | `/api/auth/change-password` | Autenticado |

### Catálogo
| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/api/categorias` | Público |
| GET | `/api/comercios` | Público |
| GET | `/api/comercios/{id}` | Público |
| GET | `/api/comercios/{id}/productos` | Público |
| GET | `/api/comercios/{id}/resenas` | Público |

### Reservas y disponibilidad
| Método | Endpoint | Acceso |
|---|---|---|
| POST | `/api/reservas` | Autenticado |
| GET | `/api/reservas/comercio/{id}` | COMERCIO |
| GET | `/api/reservas/usuario/{id}` | USUARIO |
| PUT | `/api/reservas/{id}/cancelar` | Autenticado |
| POST | `/api/disponibilidad/configurar` | COMERCIO |
| GET | `/api/disponibilidad/comercio/{id}` | Público |

### Contacto y colaboración
| Método | Endpoint | Acceso |
|---|---|---|
| POST | `/api/contacto/mensaje` | Público |
| POST | `/api/contacto/colaboracion` | Público |

### Administración
| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/api/admin/comercios-pendientes` | ADMIN |
| POST | `/api/admin/comercios/aprobar` | ADMIN |
| POST | `/api/admin/comercios/rechazar` | ADMIN |
| GET | `/api/admin/contacto/mensajes` | ADMIN |
| GET | `/api/admin/contacto/colaboraciones` | ADMIN |
| POST | `/api/admin/contacto/colaboraciones/aprobar` | ADMIN |
| POST | `/api/admin/contacto/colaboraciones/rechazar` | ADMIN |

---

## Seguridad

- Autenticación **stateless** mediante tokens **JWT**.
- Las contraseñas se almacenan con **BCrypt**.
- Los endpoints de administración (`/api/admin/**`) están restringidos al rol `ADMIN`.
- Los dashboards y operaciones críticas requieren autenticación.
- El CORS es configurable mediante variable de entorno (`APP_CORS_ALLOWED_ORIGINS`).
- El archivo `.env` con credenciales reales está excluido del control de versiones.

---

## Buenas Prácticas

1. **No modificar migraciones ya aplicadas.** Crear nuevos archivos versionados (`V3__...sql`, `V4__...sql`, etc.).
2. **Mantener el archivo `.env` fuera del repositorio.** Solo compartir `.env.example`.
3. **Usar `APP_JWT_SECRET` largo y aleatorio.** Generar con:
   ```powershell
   [Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
   ```
4. **Ejecutar las migraciones de Flyway en orden.** Se aplican automáticamente al arrancar el backend.
5. **No hardcodear URLs de entornos.** Usar variables de entorno para configuración.

---

## Documentación Relacionada

| Documento | Descripción |
|---|---|
| [ARQUITECTURA.md](ARQUITECTURA.md) | Arquitectura técnica del sistema (capas, datos, seguridad) |
| [GUIA_DESPLIEGUE.md](GUIA_DESPLIEGUE.md) | Guía paso a paso para desplegar en producción |
| [MANUAL_USO.md](MANUAL_USO.md) | Manual funcional con flujos de uso por rol |

---

## Licencia

Proyecto académico sin ánimo de lucro. TFG del ciclo formativo de **2DAW**.
