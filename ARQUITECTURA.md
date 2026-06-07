# Arquitectura DeTuBarrio

## 1. Vision general

DeTuBarrio esta organizado como una arquitectura cliente-servidor con separacion clara entre frontend y backend:

- Backend API REST en Spring Boot (carpeta `backend`)
- Frontend SPA en Vue 3 + Vite (carpeta `frontend`)
- Base de datos MySQL remota o local, gestionada por Flyway

## 2. Diagrama logico

```text
[Usuario Navegador]
       |
       | HTTP(S)
       v
[Frontend Vue (frontend)] ---------------------------.
       |                                          |
       | /api/* (proxy Vite en local)             |
       v                                          |
[Spring Boot API (backend)]                     |
       |                                          |
       | JPA + Flyway                             |
       v                                          |
[MySQL (Aiven/local)] <---------------------------'
```



## 3. Backend (Spring Boot)

### 3.1 Capas

- Controller: expone endpoints REST
- Service: logica de negocio
- Repository: acceso a datos con Spring Data JPA
- Security: autenticacion JWT y autorizacion por rol
- Config: seguridad HTTP, CORS y OpenAPI

### 3.2 Endpoints por dominio

Autenticacion:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

Catalogo:

- `GET /api/categorias`
- `POST /api/categorias`
- `GET /api/comercios`
- `GET /api/comercios/{comercioId}`
- `POST /api/comercios`
- `GET /api/comercios/{comercioId}/productos`
- `POST /api/comercios/{comercioId}/productos`

Reseñas/comentarios:

- `POST /api/comentarios`
- `GET /api/comercios/{comercioId}/resenas`
- `POST /api/comercios/{comercioId}/resenas`

Dashboard:

- `GET /api/dashboard/usuario`
- `GET /api/dashboard/comercio`
- `DELETE /api/dashboard/comercio`

Contacto:

- `POST /api/contacto/mensaje`
- `POST /api/contacto/colaboracion`

Admin:

- `GET /api/admin/comercios-pendientes`
- `POST /api/admin/comercios/aprobar`
- `POST /api/admin/comercios/rechazar`
- `GET /api/admin/contacto/mensajes`
- `GET /api/admin/contacto/colaboraciones`
- `POST /api/admin/contacto/colaboraciones/aprobar`
- `POST /api/admin/contacto/colaboraciones/rechazar`

Infra:

- `GET /api/health`
- `GET /swagger-ui.html`
- `GET /api-docs`

### 3.3 Seguridad

Segun configuracion actual:

- sesion stateless con JWT
- `register` y `login` publicos
- `me`, `dashboard` y `comentarios` autenticados
- `/api/admin/**` restringido a rol ADMIN
- CORS abierto por patron para desarrollo (`*`)

## 4. Datos y migraciones

- Base de datos: MySQL
- Migracion inicial: `V1__all_in_one.sql`
- Estrategia: `spring.jpa.hibernate.ddl-auto=validate`
- Flyway controla estructura e inserts iniciales

Implicaciones:

- la estructura se versiona con SQL (no con auto-creation de Hibernate)
- para nuevos cambios de esquema se deben crear migraciones nuevas (`V2`, `V3`, etc.)

## 5. Configuracion de entorno

El backend carga variables desde `backend/.env` mediante:

- `spring.config.import=optional:file:.env[.properties]`

Variables clave:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`
- `APP_JWT_SECRET`
- `APP_JWT_EXPIRATION`

Buenas practicas:

- mantener `.env` fuera de Git
- compartir solo `.env.example`

## 6. Frontend Vue

### 6.1 Estructura

- `frontend/src/views`: vistas por pagina
- `frontend/src/services`: cliente HTTP hacia backend
- `frontend/src/router/index.js`: rutas y guards por rol
- `frontend/vite.config.js`: proxy `/api` hacia backend local

### 6.2 Flujo de autenticacion

- login en `/login`
- se guarda token JWT en `localStorage` (`detubarrio_auth`)
- interceptor axios añade `Authorization: Bearer ...`
- guard de rutas redirige segun rol

## 7. Decisiones de arquitectura

- Monorepo con backend + frontend para desarrollo integrado
- Flyway como fuente de verdad del esquema
- JWT para autenticacion sin estado en servidor
- Separacion clara de capas para mantenibilidad
- Configuracion por variables de entorno para despliegues flexibles

## 8. Riesgos tecnicos y recomendaciones

Riesgos actuales:

- CORS demasiado abierto para produccion
- riesgo de duplicacion si se reintroduce frontend legacy
- falta de pipeline de pruebas automatizadas visible en repositorio

Recomendaciones:

1. endurecer CORS y cabeceras para produccion
2. mantener un solo frontend (Vue) como unico cliente
3. definir flujo de migraciones incremental (sin editar historico)
4. incorporar tests de integracion de endpoints criticos
5. documentar checklist de release y rollback
