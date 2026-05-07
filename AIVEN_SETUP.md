# Despliegue de DeTuBarrio en Aiven

## Paso 1: Crear MySQL en Aiven (BASE DE DATOS COMPARTIDA)

1. Ve a https://aiven.io y regístrate/inicia sesión.
2. Crea un nuevo proyecto.
3. Añade un servicio de tipo MySQL.
4. Aiven crea la instancia y te muestra los datos de conexión del servicio.
5. En la sección de conexión copia estos valores:
   - `MYSQLHOST`: host de la BD
   - `MYSQLPORT`: puerto, normalmente 3306
   - `MYSQLDATABASE`: nombre de la BD
   - `MYSQLUSER`: usuario de BD
   - `MYSQLPASSWORD`: contraseña de BD

## Paso 2: Configurar `.env` local con esos datos

En tu máquina local, edita `rest/rest/.env`:

```
DB_URL=jdbc:mysql://MYSQLHOST_VALUE:MYSQLPORT_VALUE/MYSQLDATABASE_VALUE?ssl-mode=REQUIRED&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USER=MYSQLUSER_VALUE
DB_PASSWORD=MYSQLPASSWORD_VALUE
APP_JWT_SECRET=detubarrio_secreto_compartido_2026
APP_JWT_EXPIRATION=86400
```

Ejemplo real con valores de Aiven:
```
DB_URL=jdbc:mysql://mysql-prod-abc123.aivencloud.com:12345/defaultdb?ssl-mode=REQUIRED&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USER=avnadmin
DB_PASSWORD=abc123xyz456
APP_JWT_SECRET=detubarrio_secreto_compartido_2026
APP_JWT_EXPIRATION=86400
```

**Importante:** `APP_JWT_SECRET` no es un dato de Aiven; es un secreto del backend. Pon un valor largo y aleatorio en `rest/rest/.env` y usa exactamente el mismo en todos los backends que compartan sesión JWT.

Si quieres generarlo rápido en PowerShell:

```powershell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
```

Después copia el resultado en `APP_JWT_SECRET` de tu `.env` y repítelo igual en el backend de la otra máquina.

## Paso 3: Arrancar backend local contra BD remota

En tu máquina:

```powershell
cd rest/rest
.\mvnw.cmd spring-boot:run
```

Spring carga el `.env` automáticamente. Verás en los logs:
- Conexión a la BD remota de Aiven
- Flyway aplicando migraciones
- Datos base cargados

## Paso 4: Arrancar frontend local

En otra terminal:

```powershell
cd a/vue
npm install  # Primera vez
npm run dev
```

Accede a http://localhost:5173

## Resultado esperado

- Ambos desarrolladores usan el mismo `.env`
- La BD está en Aiven (persistente, remota y compartida)
- Backend y frontend corren locales en cada máquina
- Todos los cambios de datos se ven en ambas máquinas porque apuntan a la misma BD

## Para desplegar producción más adelante

Cuando estéis listos:
1. Desplegar backend en un entorno de producción que apunte al MySQL de Aiven.
2. Desplegar frontend en Vercel o Netlify apuntando al backend.
3. Mantener la BD de Aiven como base compartida.

## Copia de seguridad de la BD

Antes de cambios grandes, haced backup:

```bash
mysqldump -h MYSQLHOST -u MYSQLUSER -p MYSQLDATABASE > backup.sql
```

Si hace falta restaurar:

```bash
mysql -h MYSQLHOST -u MYSQLUSER -p MYSQLDATABASE < backup.sql
```
