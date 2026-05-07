# DeTuBarrio - MVP Backend + Frontend.

Aplicacion TFG (2DAW) con API REST, autenticacion JWT, reseñas y frontend Bootstrap conectado.

## Stack tecnologico

- Java 21
- Spring Boot 3 (Web, JPA, Security, Validation)
- MySQL (una sola configuración para desarrollo y despliegue)
- Maven
- Bootstrap 5 + HTML + CSS + JavaScript vanilla
- OpenAPI/Swagger

## Funcionalidad MVP implementada

- Catalogo de categorias, comercios y productos
- Reseñas/comentarios desde frontend y API
- Autenticacion JWT (login/register/me)
- Dashboards por rol (`USUARIO`, `COMERCIO`)
- Documentacion API con Swagger UI
- Datos semilla para demo local

## Estructura

- `src/main/java/detubarrio/rest/config`: seguridad, CORS, OpenAPI y datos semilla.
- `src/main/java/detubarrio/rest/controller`: endpoints REST.
- `src/main/java/detubarrio/rest/service`: logica de negocio.
- `src/main/java/detubarrio/rest/repository`: acceso a datos.
- `src/main/java/detubarrio/rest/model`: entidades JPA.
- `src/main/java/detubarrio/rest/dto`: contratos de API.
- `src/main/java/detubarrio/rest/security`: JWT y filtro de autenticacion.
- `src/main/java/detubarrio/rest/exception`: manejo global de errores.
- `src/main/resources/static`: frontend servido por Spring Boot.

## Arranque local (Windows PowerShell)

Ejecuta en esta carpeta (`rest/rest`):

```powershell
.\mvnw.cmd spring-boot:run
```

## Como conectar el backend con MySQL

El backend usa una sola configuración en `rest/rest/src/main/resources/application.properties`.

Por defecto conecta con MySQL local en `localhost:3307`:

- Base de datos: `detubarrio`
- Usuario: `detubarrio`
- Contraseña: `detubarrio123`

Si quieres conectarlo a una MySQL remota cuando despliegues, solo tienes que cambiar estas variables de entorno:

```powershell
$env:DB_URL="jdbc:mysql://tu-servidor:3306/detubarrio?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USER="tu_usuario"
$env:DB_PASSWORD="tu_password"
```

Si despliegas en Docker o en un VPS, esas variables se ponen en el panel del hosting, en el `.env` o en la configuración del servicio.

## Que usar cuando presentes

- Durante tus pruebas: usa la MySQL local que tengas instalada en tu portátil.
- Cuando lo despliegues: usa la MySQL remota y cambia solo `DB_URL`, `DB_USER` y `DB_PASSWORD`.

No necesitas cambiar de perfil ni usar H2.

Importante:

- No cierres esa terminal: si la cierras, se detiene el backend.
- Abre otra terminal para hacer pruebas (`Terminal > New Terminal`).

## Validacion rapida (smoke test)

Pega en una terminal nueva:

```powershell
# Health
Invoke-RestMethod "http://localhost:8080/api/health" | ConvertTo-Json -Compress

# Listar comercios
Invoke-RestMethod "http://localhost:8080/api/comercios" | ConvertTo-Json -Depth 5

# Login usuario semilla
$loginBody = @{ email='ana@detubarrio.local'; password='123456' } | ConvertTo-Json
$login = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/auth/login" -ContentType "application/json" -Body $loginBody
$login | ConvertTo-Json -Compress

# Endpoint protegido /me
$headers = @{ Authorization = "Bearer $($login.token)" }
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/me" -Headers $headers | ConvertTo-Json -Compress

# Crear comentario/reseña
$body = @{ comercioId=1; titulo='Prueba'; comentario='Funciona'; valoracion=5; autorNombre='QA'; autorEmail='qa@example.com' } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/comentarios" -ContentType "application/json; charset=utf-8" -Body $body | ConvertTo-Json -Compress
```

Si todas responden bien, el MVP backend+auth+reviews esta operativo.

## Swagger

Con el backend encendido:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/v3/api-docs`

## Frontend para demo

- `http://localhost:8080/login_db.html`
- `http://localhost:8080/gestion_usuario.html`
- `http://localhost:8080/gestion_comercio.html`
- `http://localhost:8080/comercio_individual.html?id=1`

## Base de datos

- La base de datos ya no depende de H2.
- Flyway aplica la migración única `V1__all_in_one.sql` al arrancar el backend.
- El arranque no depende de `Script_corregido.sql` ni de scripts manuales sueltos.

## Siguientes mejoras recomendadas

** Ahora mismo el desarrollo del proyecto esta en fase beta, es decir hay cierta parte funcional ya pero requiere de tiempo para pulir y desarrollar todo lo demas **

1. Arreglar el footer de listado comercios
2. Implementar logica de codigo para funcionamiento de otros elementos
3. Mejorar tema de usabilidad y accesibilidad en el tema de las cosas poder acceder de mejor manera
4. Añadir tests de integracion de auth, dashboard y comentarios.
5. Documentar capturas de flujo en memoria del TFG (login, reseña, Swagger).
6. Preparar despliegue (Docker o VPS) para demostracion final.
..
....
......
........
..........