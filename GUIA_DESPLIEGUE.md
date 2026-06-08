# Guía de Despliegue — DeTuBarrio

**Arquitectura de despliegue:**

| Componente | Plataforma | Servicio |
|---|---|---|
| Backend (API REST) | Render | Web Service con Docker |
| Frontend (SPA Vue) | Vercel | Proyecto Vite |
| Base de datos | Aiven | MySQL 8.x |
| Imágenes | Cloudinary | Almacenamiento cloud |
| Email SMTP | Gmail | Servicio de correo |

---

## Requisitos Previos

- Repositorio en **GitHub** con el código actualizado.
- Cuenta gratuita en **Render** (<https://render.com>).
- Cuenta gratuita en **Vercel** (<https://vercel.com>).
- Cuenta gratuita en **Aiven** (<https://aiven.io>).
- Cuenta gratuita en **Cloudinary** (<https://cloudinary.com>).
- Cuenta de **Gmail** con contraseña de aplicación configurada (para envío de emails).

---

## 1. Preparación del Repositorio

Antes de desplegar, asegurarse de que todos los archivos necesarios están en el repositorio remoto:

```bash
git add .
git commit -m "Preparacion para despliegue: Dockerfile, Vercel, CORS configurable"
git push origin main
```

El repositorio ya incluye los archivos necesarios para el despliegue:
- `backend/dockerfile` — contenedor multi-etapa para Spring Boot.
- `frontend/vercel.json` — configuración de build para Vercel.
- `backend/.env.example` — plantilla de variables de entorno.

---

## 2. Base de Datos (Aiven MySQL)

### 2.1 Crear el servicio

1. Iniciar sesión en [Aiven Console](https://console.aiven.io).
2. Crear un nuevo servicio **MySQL**.
3. Seleccionar el plan **Free** (suficiente para desarrollo y pruebas).
4. Elegir la región más cercana.
5. Una vez creado, anotar los siguientes datos de conexión:
   - **Host** (ej. `mysql-...aivencloud.com`)
   - **Puerto** (ej. `10258`)
   - **Usuario** (ej. `avnadmin`)
   - **Contraseña**

### 2.2 URI de conexión JDBC

```
jdbc:mysql://<HOST>:<PUERTO>/defaultdb?sslMode=REQUIRED
```

> **Importante**: la conexión requiere SSL. El parámetro `sslMode=REQUIRED` es obligatorio.

### 2.3 Migraciones

Flyway aplicará automáticamente las migraciones (`V1__all_in_one.sql` y `V2__seed_data.sql`) al arrancar el backend por primera vez. No es necesario ejecutar nada manualmente.

---

## 3. Backend (Render)

### 3.1 Crear el Web Service

1. Iniciar sesión en [Render Dashboard](https://dashboard.render.com).
2. Hacer clic en **New +** → **Web Service**.
3. Conectar la cuenta de GitHub y seleccionar el repositorio `DetuBarrio`.
4. Configurar los siguientes parámetros:

| Parámetro | Valor |
|---|---|
| **Name** | `detubarrio-api` |
| **Root Directory** | `backend` |
| **Runtime** | `Docker` (Render lo detectará automáticamente por el Dockerfile) |
| **Branch** | `main` |
| **Region** | La más cercana a vosotros (ej. `Frankfurt`) |
| **Plan** | `Free` |

### 3.2 Variables de Entorno

Añadir en la sección **Environment Variables**:

```
DB_URL=jdbc:mysql://<HOST>:<PUERTO>/defaultdb?sslMode=REQUIRED
DB_USER=<usuario>
DB_PASSWORD=<contraseña>
APP_JWT_SECRET=<clave-secreta-segura>
APP_JWT_EXPIRATION=86400
APP_CORS_ALLOWED_ORIGINS=*
CLOUDINARY_CLOUD_NAME=<cloud-name>
CLOUDINARY_API_KEY=<api-key>
CLOUDINARY_API_SECRET=<api-secret>
CLOUDINARY_FOLDER=detubarrio
APP_FRONTEND_URL=https://detubarrio.vercel.app
EMAIL_USER=dtubarrio@gmail.com
EMAIL_PASSWORD=<contraseña-de-aplicacion-gmail>
CONTACT_ADMIN_EMAIL=<email-admin>
CONTACT_FROM_EMAIL=no-reply@detubarrio.local
```

> **Nota**: `EMAIL_PASSWORD` debe ser una **contraseña de aplicación** de Gmail, no la contraseña normal de la cuenta. Si no se requiere envío de emails, el backend funciona igualmente.

### 3.3 Desplegar

Hacer clic en **Deploy Web Service**. La primera vez, Render construirá la imagen Docker (5-10 minutos).

### 3.4 Verificar

Una vez finalizado el despliegue, Render proporcionará una URL similar a:

```
https://detubarrio-api.onrender.com
```

Verificar que el backend funciona:

```
https://detubarrio-api.onrender.com/api/health
```

Respuesta esperada:
```json
{ "status": "OK", "baseDeDatos": "Conectada" }
```

También está disponible la documentación Swagger en:
```
https://detubarrio-api.onrender.com/swagger-ui.html
```

---

## 4. Frontend (Vercel)

### 4.1 Importar el proyecto

1. Iniciar sesión en [Vercel Dashboard](https://vercel.com/dashboard).
2. Hacer clic en **Add New** → **Project**.
3. Conectar GitHub y seleccionar el repositorio `DetuBarrio`.
4. Configurar:

| Parámetro | Valor |
|---|---|
| **Root Directory** | `frontend` |
| **Framework Preset** | `Vite` (se detecta automáticamente) |
| **Build Command** | `npm run build` (por defecto) |
| **Output Directory** | `dist` (por defecto) |

### 4.2 Variable de Entorno

Añadir en **Environment Variables**:

```
VITE_API_URL=https://detubarrio-api.onrender.com
```

> Sustituir por la URL real obtenida de Render.

### 4.3 Desplegar

Hacer clic en **Deploy**. Vercel construirá el frontend y en aproximadamente 2 minutos estará disponible en una URL similar a:

```
https://detubarrio.vercel.app
```

### 4.4 Verificar

1. Abrir la URL de Vercel en el navegador.
2. Comprobar que la página principal carga correctamente.
3. Navegar por las categorías y comercios.
4. Probar el registro e inicio de sesión.
5. Verificar que los formularios de contacto funcionan.

---

## 5. Configuración de CORS (Recomendado)

Una vez que el frontend esté desplegado, restringir CORS en el backend para mayor seguridad:

1. Ir a **Render Dashboard** → **detubarrio-api** → **Environment**.
2. Localizar `APP_CORS_ALLOWED_ORIGINS`.
3. Cambiar de `*` a la URL del frontend en Vercel:
   ```
   APP_CORS_ALLOWED_ORIGINS=https://detubarrio.vercel.app
   ```
4. Guardar los cambios.
5. Ir a **Manual Deploy** → **Deploy latest image** para reiniciar el backend.

> Para múltiples orígenes, separar con comas:
> `APP_CORS_ALLOWED_ORIGINS=https://detubarrio.vercel.app,https://detubarrio.com`

---

## 6. Cloudinary (Imágenes)

### 6.1 Obtener credenciales

1. Iniciar sesión en [Cloudinary Dashboard](https://cloudinary.com/console).
2. Anotar los siguientes valores de la cuenta:
   - **Cloud name** (`CLOUDINARY_CLOUD_NAME`)
   - **API Key** (`CLOUDINARY_API_KEY`)
   - **API Secret** (`CLOUDINARY_API_SECRET`)
3. Configurar la carpeta de subida (`CLOUDINARY_FOLDER=detubarrio`).

### 6.2 Configurar en Render

Añadir o verificar las variables de entorno `CLOUDINARY_*` en Render (sección 3.2).

---

## 7. Limitaciones de los Planes Gratuitos

### Render (Backend)
- El servicio se **duerme** después de 15 minutos sin actividad.
- En la primera petición tras el periodo de inactividad, tarda 30-60 segundos en "despertarse".
- Para mantenerlo siempre activo, es necesario el plan de pago ($7/mes).

### Vercel (Frontend)
- Sin limitaciones significativas para proyectos de este tamaño.
- Los despliegues se realizan automáticamente al hacer push a la rama configurada.

### Aiven (Base de datos)
- Máximo **5 conexiones simultáneas** a la base de datos.
- Suficiente para desarrollo y demostraciones. Para producción real se recomienda un plan superior.

### Cloudinary (Imágenes)
- Plan gratuito: 25 GB de almacenamiento, 25 GB de ancho de banda mensual.
- Más que suficiente para este proyecto.

---

## 8. Solución de Problemas

| Problema | Causa probable | Solución |
|---|---|---|
| Error 503 en Render | El contenedor está construyéndose o reiniciándose | Esperar unos minutos y recargar |
| Error 502 Bad Gateway | El backend tarda en arrancar | Esperar 30-60 segundos y recargar |
| `ERR_CONNECTION_REFUSED` | El contenedor se durmió por inactividad | Recargar la página y esperar |
| Las imágenes no se cargan | Cloudinary mal configurado | Revisar `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` |
| Error de CORS | `APP_CORS_ALLOWED_ORIGINS` no incluye la URL del frontend | Cambiar de `*` a la URL de Vercel |
| Error de login | La base de datos no está conectada | Verificar `DB_URL`, `DB_USER`, `DB_PASSWORD` en Render |
| Página en blanco en Vercel | `VITE_API_URL` no configurada o incorrecta | Revisar la variable de entorno en Vercel |
| Error de migración Flyway | Conflicto en el orden de migraciones | Verificar que no se modifican migraciones ya aplicadas |

---

## 9. Checklist de Despliegue

- [ ] Repositorio en GitHub con el código actualizado.
- [ ] Servicio MySQL creado en Aiven con credenciales anotadas.
- [ ] Backend desplegado en Render y verificando con `/api/health`.
- [ ] Frontend desplegado en Vercel y cargando correctamente.
- [ ] Variable `VITE_API_URL` en Vercel apuntando al backend de Render.
- [ ] CORS configurado con la URL del frontend (tras el despliegue).
- [ ] Cloudinary configurado y verificando carga de imágenes.
- [ ] Flujo completo de registro, login y navegación probado.

---


