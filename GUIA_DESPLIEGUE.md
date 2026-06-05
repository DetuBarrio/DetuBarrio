# Guía de Despliegue — DeTuBarrio

Backend → Render · Frontend → Vercel · Imágenes → Cloudinary · BD → Aiven MySQL

---

## Requisitos antes de empezar

- Repositorio en GitHub (ya deberías tenerlo)
- Cuenta en [Render](https://render.com) (gratuita)
- Cuenta en [Vercel](https://vercel.com) (gratuita)
- Cuenta en [Aiven](https://aiven.io) (gratuita) 
- Cuenta en [Cloudinary](https://cloudinary.com) (gratuita) 

---

## 1. Subir los cambios a GitHub

Antes de desplegar, los archivos nuevos (Dockerfile, vercel.json, etc.) tienen que estar en el repositorio remoto.

```bash
git add .
git commit -m "Preparacion para despliegue: Dockerfile, Vercel, CORS configurable"
git push origin devel_jjcastilla
```

> Si tu rama se llama distinto (`main` o `master`), ajusta el nombre.

---

## 2. Desplegar el Backend en Render

Render va a coger el código desde GitHub y lo va a empaquetar con el Dockerfile para crear un contenedor con el backend Spring Boot.

### 2.1 Crear el Web Service

1. Inicia sesión en [Render Dashboard](https://dashboard.render.com)
2. Haz clic en **"New +"** → **"Web Service"**
3. Conecta tu cuenta de GitHub y selecciona el repositorio `DetuBarrio`
4. Configura estos campos:

| Campo | Valor |
|---|---|
| **Name** | `detubarrio-api` |
| **Root Directory** | `rest/rest` |
| **Runtime** | `Docker` (Render lo detectará automáticamente por el Dockerfile) |
| **Branch** | `devel_jjcastilla` (o la que uses) |
| **Region** | La más cercana a vosotros (ej. `Frankfurt`) |
| **Plan** | `Free` |

### 2.2 Añadir variables de entorno

En la misma pantalla de creación, baja a la sección **"Environment Variables"** y añade todas estas:

```
DB_URL=jdbc:mysql://mysql-298b0850-dtubarrio-975a.b.aivencloud.com:10258/defaultdb?sslMode=REQUIRED
DB_USER=avnadmin
DB_PASSWORD=AVNS_2x5tsPhvX7ZLY5dDp_U
APP_JWT_SECRET=DetuBarrioSecretKeyMuySegura2026
APP_JWT_EXPIRATION=86400
APP_CORS_ALLOWED_ORIGINS=*
CLOUDINARY_CLOUD_NAME=dqewfn7gt
CLOUDINARY_API_KEY=212171983935936
CLOUDINARY_API_SECRET=JmP_F2Ndxuo6Fb2s0loTqzvAKtk
CLOUDINARY_FOLDER=detubarrio
EMAIL_USER=dtubarrio@gmail.com
EMAIL_PASSWORD=pon_aqui_tu_contraseña_de_app
CONTACT_ADMIN_EMAIL=pon-tu-email@ejemplo.com
CONTACT_FROM_EMAIL=no-reply@detubarrio.local
```

> **Importante**: en `EMAIL_PASSWORD` tienes que poner una contraseña de aplicación de Gmail (no la contraseña normal de tu cuenta). Si no usáis el envío de email en producción, no pasa nada, el backend funciona igual sin ello.

2.3 Haz clic en **"Deploy Web Service"**

Render empezará a construir el contenedor (tarda unos 5-10 minutos la primera vez).

### 2.4 Obtener la URL del backend

Cuando termine, Render te mostrará una URL similar a:

```
https://detubarrio-api.onrender.com
```

**Guárdala**, la necesitarás para el frontend.

### 2.5 Verificar que el backend funciona

Abre en tu navegador: `https://detubarrio-api.onrender.com/api/health`

Tienes que ver algo como:
```json
{ "status": "OK", "baseDeDatos": "Conectada" }
```

También puedes abrir: `https://detubarrio-api.onrender.com/swagger-ui.html` para ver la documentación de la API.

---

## 3. Desplegar el Frontend en Vercel

### 3.1 Importar el proyecto

1. Inicia sesión en [Vercel Dashboard](https://vercel.com/dashboard)
2. Haz clic en **"Add New"** → **"Project"**
3. Conecta GitHub y selecciona el repositorio `DetuBarrio`
4. Configura estos campos:

| Campo | Valor |
|---|---|
| **Root Directory** | `a/vue` (haz clic en **"Edit"** y selecciona la carpeta `a/vue`) |
| **Framework Preset** | `Vite` (se detectará automático gracias al `vercel.json`) |
| **Build Command** | `npm run build` (se rellena solo) |
| **Output Directory** | `dist` (se rellena solo) |

### 3.2 Añadir variable de entorno

En la sección **"Environment Variables"**, añade:

```
VITE_API_URL = https://detubarrio-api.onrender.com
```

> Sustituye `https://detubarrio-api.onrender.com` por la URL real que te haya dado Render en el paso 2.4.

### 3.3 Desplegar

Haz clic en **"Deploy"**.

Vercel construirá el frontend y en un par de minutos te dará una URL similar a:

```
https://detubarrio.vercel.app
```

### 3.4 Verificar que el frontend funciona

Abre la URL de Vercel en tu navegador. Deberías ver la página principal de DeTuBarrio.

Prueba a:
- Navegar por las categorías y comercios
- Hacer login con un usuario de prueba
- Registrarte

---

## 4. Ajustar CORS (opcional pero recomendado)

Una vez que tengas la URL de Vercel, puedes restringir CORS en el backend para que solo acepte peticiones desde tu frontend.

1. Ve a **Render Dashboard** → **detubarrio-api** → **Environment**
2. Localiza la variable `APP_CORS_ALLOWED_ORIGINS`
3. Cambia su valor de `*` a la URL de tu frontend en Vercel:
   ```
   APP_CORS_ALLOWED_ORIGINS=https://detubarrio.vercel.app
   ```
4. Haz clic en **"Save Changes"**
5. Ve a la pestaña **"Manual Deploy"** → **"Deploy latest image"** para reiniciar el backend y que coja el cambio

> Si en el futuro añades otro frontend (ej. un dominio personalizado), puedes separar las URLs con coma:
> `APP_CORS_ALLOWED_ORIGINS=https://detubarrio.vercel.app,https://detubarrio.com`

---

## 5. Cosas a tener en cuenta

### Plan gratuito de Render
- El backend se duerme después de 15 minutos sin actividad
- Cuando alguien accede, tarda unos 30-60 segundos en "despertarse" (es normal)
- Si queréis que esté siempre activo, tendréis que pagar ($7/mes)

### Plan gratuito de Vercel
- Sin limitaciones importantes para un proyecto como este
- Los despliegues se hacen automáticamente cada vez que hacéis push a la rama

### Plan gratuito de Aiven
- Máximo 5 conexiones simultáneas a la base de datos
- Suficiente para desarrollo y pruebas, pero en producción real necesitaríais un plan de pago

### Cloudinary
- Plan gratuito: 25GB de almacenamiento, 25GB de ancho de banda
- Más que suficiente para este proyecto

---

## 6. Solución de problemas

| Problema | Causa probable | Solución |
|---|---|---|
| Error 503 en Render | El contenedor está construyéndose | Espera unos minutos |
| Error 502 Bad Gateway | El backend tarda en arrancar | Espera 30s y recarga |
| `ERR_CONNECTION_REFUSED` | El contenedor se durmió | Recarga la página y espera |
| Las imágenes no se cargan | Cloudinary mal configurado | Revisa `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` en Render |
| Error de CORS | `APP_CORS_ALLOWED_ORIGINS` no incluye la URL de Vercel | Cámbiala de `*` a la URL de Vercel |
| Login falla | La BD no está conectada | Revisa `DB_URL`, `DB_USER`, `DB_PASSWORD` en Render |
| La página en blanco en Vercel | `VITE_API_URL` no está configurada o es incorrecta | Revisa la variable de entorno en Vercel |
| Error 404 en rutas | Al ir a una URL directamente | El frontend usa hash routing (`#/`), así que no debería pasar. Si ocurre, avísame. |
