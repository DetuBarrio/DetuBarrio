# AGENTS.md - DeTuBarrio

## Project Structure

```
DetuBarrio/
├── rest/rest/                    # Spring Boot API (Maven, Java 21)
│   ├── src/main/java/           # Backend code
│   ├── src/main/resources/
│   │   ├── application*.properties
│   │   └── static/              # Legacy HTML/Bootstrap frontend
│   └── pom.xml
├── a/vue/                        # Vue 3 frontend (Vite)
├── Script_corregido.sql         # MySQL schema reference
└── ARQUITECTURA.md              # Detailed architecture docs
```

## Developer Commands

### Backend (Spring Boot)
```powershell
cd rest/rest
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```
- Port: 8080
- Uses H2 in-memory database with seed data
- Swagger: http://localhost:8080/swagger-ui.html

### Frontend (Vue 3)
```powershell
cd a/vue
npm run dev
```
- Port: 5173
- Proxies `/api` → `http://localhost:8080`

## Test Credentials
- Email: `ana@detubarrio.local`
- Password: `123456`
- Other seed users in `DataSeederConfig.java`

## Important Notes

- Backend and Vue frontend run on different ports; use the Vue dev server to avoid CORS issues
- The `local` profile uses H2 (data resets on restart); production profile uses MySQL
- `application.properties` defaults to MySQL on port 3307
- Legacy HTML frontend is served by Spring Boot at `http://localhost:8080/`
- JWT secret and expiration are in `application*.properties`

## Tech Stack
- Java 21, Spring Boot 3, Spring Security, JWT
- Vue 3 + Vite + Axios + Bootstrap
- H2 (dev) / MySQL (prod)
- Spring Data JPA, Lombok