# Manual de Uso — DeTuBarrio

## 1. Introducción

**DeTuBarrio** es una plataforma web que pone en contacto a los usuarios con los comercios de su barrio. Permite descubrir negocios locales, consultar sus productos y servicios, realizar reservas online, y gestionar la actividad desde paneles de control adaptados a cada perfil.

La aplicación cuenta con **tres roles de usuario**: `USUARIO`, `COMERCIO` y `ADMIN`, cada uno con funcionalidades y permisos específicos.

---

## 2. Acceso a la Aplicación

### Entorno de desarrollo

| Componente | URL |
|---|---|
| Frontend (Vue) | `http://localhost:5173` |
| Backend (API) | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |

### Arranque del backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

### Arranque del frontend

```powershell
cd frontend
npm install
npm run dev
```

---

## 3. Roles y Permisos

| Rol | Descripción | Acceso principal |
|---|---|---|
| **USUARIO** | Usuario registrado que consume servicios | Panel de usuario |
| **COMERCIO** | Titular de un negocio registrado en la plataforma | Panel de comercio |
| **ADMIN** | Administrador del sistema | Panel de administración |
| **Invitado** | Usuario no autenticado | Solo secciones públicas |

---

## 4. Funcionalidades por Rol

### 4.1 Invitado (sin autenticación)

- Navegar por la página de inicio.
- Explorar el catálogo de **categorías y comercios**.
- Ver el **detalle de un comercio**: productos, horarios, reseñas.
- Consultar las secciones informativas: **FAQ**, **Términos y condiciones**, **Política de privacidad**, **Acerca de**.
- Enviar un **mensaje de contacto** o **solicitud de colaboración**.
- **Registrarse** como nuevo usuario o **iniciar sesión**.

### 4.2 USUARIO (autenticado)

#### Panel de usuario (`/dashboard/usuario`)

- **Panel general**: visión general de la actividad del usuario.
- **Mis reservas**: consultar, gestionar y cancelar reservas realizadas en comercios.
- **Mi perfil**: editar datos personales y preferencias.
- **Ajustes**: configuración de la cuenta.
- **Favoritos**: gestionar la lista de comercios favoritos.

#### Flujo de reserva

1. Navegar hasta la ficha de un comercio.
2. Consultar la **disponibilidad horaria** del comercio.
3. Seleccionar una fecha y franja horaria disponible.
4. Confirmar la reserva.
5. Gestionar la reserva desde el panel de usuario.

### 4.3 COMERCIO (autenticado)

Para obtener el rol de comercio, el negocio debe ser registrado y aprobado por un administrador.

#### Panel de comercio (`/dashboard/comercio`)

- **Panel general**: estadísticas y resumen de la actividad del comercio.
- **Reservas**: consultar todas las reservas recibidas, gestionar su estado.
- **Horarios / Disponibilidad**: configurar las franjas horarias disponibles para reservas.
- **Clientes**: consultar el listado de clientes que han interactuado con el comercio.
- **Configuración**: editar los datos del comercio, productos e imágenes.

#### Gestión de disponibilidad

1. Acceder a la sección **Horarios** del panel.
2. Configurar los días y franjas horarias en las que el comercio acepta reservas.
3. Los horarios configurados se reflejan automáticamente en la ficha pública del comercio.

#### Gestión de productos

1. Acceder a la **Configuración** del panel de comercio.
2. Añadir, editar o eliminar productos/servicios.
3. Cada producto puede incluir nombre, descripción, precio e imagen.

### 4.4 ADMIN (autenticado)

#### Panel de administración (`/admin`)

- **Solicitudes de comercios**: revisar, aprobar o rechazar las solicitudes de registro de nuevos comercios.
- **Gestión de mensajes**: consultar los mensajes enviados desde el formulario de contacto.
- **Gestión de colaboraciones**: revisar, aprobar o rechazar las solicitudes de colaboración recibidas.
- **Listado de comercios**: eliminar comercios del sistema si es necesario.

#### Flujo de aprobación de comercio

1. Un comercio envía una **solicitud de colaboración** desde el formulario de contacto.
2. El administrador accede al panel y revisa la solicitud.
3. Si los datos son correctos, el administrador puede **aprobar** la solicitud.
4. Al aprobar, se crea automáticamente la cuenta de comercio y se notifica al solicitante.
5. Si procede, el administrador puede **rechazar** la solicitud indicando un motivo.

---

## 5. Flujos Funcionales Recomendados (Demo)

### Flujo A: Navegación pública

1. Abrir la página de inicio.
2. Explorar las **categorías** de comercios disponibles.
3. Seleccionar una categoría para filtrar los resultados.
4. Acceder a la **ficha de un comercio**.
5. Consultar sus **productos**, **reseñas** y **horarios**.

### Flujo B: Autenticación y perfil

1. Registrarse como nuevo usuario.
2. Iniciar sesión con las credenciales creadas.
3. Acceder al **panel de usuario**.
4. Editar el **perfil** y explorar las secciones del dashboard.
5. Cerrar sesión.

### Flujo C: Reserva online

1. Iniciar sesión como usuario.
2. Navegar hasta un comercio que tenga disponibilidad configurada.
3. Seleccionar una **fecha y hora** disponible.
4. Confirmar la **reserva**.
5. Ver la reserva en el panel de usuario.
6. (Opcional) Cancelar la reserva.

### Flujo D: Administración

1. Iniciar sesión con una cuenta `ADMIN`.
2. Acceder al **panel de administración**.
3. Revisar las **solicitudes de comercios** pendientes.
4. Aprobar o rechazar una solicitud.
5. Consultar los **mensajes de contacto** recibidos.
6. Gestionar las **solicitudes de colaboración**.

### Flujo E: Gestión de comercio

1. Iniciar sesión con una cuenta `COMERCIO`.
2. Acceder al **panel de comercio**.
3. Configurar los **horarios de disponibilidad**.
4. Añadir **productos** al catálogo del comercio.
5. Consultar las **reservas** recibidas.
6. Revisar el listado de **clientes**.

---

## 6. Pruebas Rápidas por API (Smoke Tests)

### Health check
```powershell
Invoke-RestMethod "http://localhost:8080/api/health" | ConvertTo-Json -Compress
```

### Listar categorías
```powershell
Invoke-RestMethod "http://localhost:8080/api/categorias" | ConvertTo-Json -Depth 5
```

### Listar comercios
```powershell
Invoke-RestMethod "http://localhost:8080/api/comercios" | ConvertTo-Json -Depth 5
```

### Iniciar sesión
```powershell
$loginBody = @{ email='admin@detubarrio.local'; password='admin123' } | ConvertTo-Json
$login = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/auth/login" -ContentType "application/json" -Body $loginBody
$login | ConvertTo-Json -Compress
```

### Endpoint protegido (requiere token)
```powershell
$headers = @{ Authorization = "Bearer $($login.token)" }
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/me" -Headers $headers | ConvertTo-Json -Compress
```

---

## 7. Casos de Error Habituales

| Error | Causa probable | Solución |
|---|---|---|
| `401 Unauthorized` | Token JWT ausente, inválido o expirado | Iniciar sesión de nuevo |
| `403 Forbidden` | El rol del usuario no tiene permisos para el recurso | Verificar que se usa la cuenta correcta |
| `500 Internal Server Error` | Error en el backend | Revisar logs del servidor |
| Error de conexión a la BD | Base de datos no accesible | Verificar `DB_URL`, `DB_USER`, `DB_PASSWORD` |
| Error de migración Flyway | Conflicto entre migraciones existentes y nuevas | Revisar el orden y contenido de las migraciones |
| Las imágenes no cargan | Cloudinary mal configurado | Verificar credenciales de Cloudinary |
| Error de CORS | El origen no está permitido | Configurar `APP_CORS_ALLOWED_ORIGINS` |

---

## 8. Checklist de Operación Diaria

1. [ ] El backend está en ejecución en `localhost:8080`.
2. [ ] El frontend está en ejecución en `localhost:5173`.
3. [ ] El endpoint `/api/health` responde `OK`.
4. [ ] Se puede iniciar sesión con credenciales de prueba.
5. [ ] El catálogo de comercios carga correctamente.
6. [ ] Los formularios de contacto y colaboración funcionan.
7. [ ] Los paneles de usuario, comercio y admin responden según el rol.

---

## 9. Estado del Proyecto y Próximas Mejoras

### Estado actual
- Autenticación JWT con registro, login y recuperación de contraseña.
- Catálogo completo de categorías y comercios con filtrado.
- Ficha de comercio con productos, reseñas y horarios.
- Sistema de reservas online con gestión de disponibilidad.
- Paneles role-based (usuario, comercio, administración).
- Gestión de solicitudes de colaboración y mensajes de contacto.
- Subida de imágenes a Cloudinary.
- Generación de documentos PDF.
- Notificaciones por email.
- API documentada con Swagger/OpenAPI.
- Despliegue preparado para Render + Vercel.

### Próximas mejoras sugeridas
1. **Tests automatizados**: integración de tests unitarios y de integración en backend y frontend.
2. **Seguridad en producción**: endurecimiento de cabeceras HTTP y rate limiting.
3. **Auditoría**: trazabilidad de acciones realizadas por administradores.
4. **Accesibilidad**: mejoras en contraste, navegación por teclado y lectores de pantalla.
5. **Pipeline CI/CD**: automatización de builds y despliegues.
