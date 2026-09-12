# Gestor de Tareas

Aplicación full-stack de gestión de tareas con autenticación, categorías y un
tablero Kanban con drag & drop.

<!-- TODO: agregar una captura del tablero acá antes de subir a GitHub -->

## Stack

- **Backend**: Java 21 + Spring Boot 4, Spring Data JPA, Spring Security (JWT), PostgreSQL
- **Frontend**: React + TypeScript (Vite), @dnd-kit para el drag & drop

## Arquitectura

El backend sigue una arquitectura en capas clásica:

```
Controller  →  Service  →  Repository  →  Entity
   (HTTP)      (lógica)     (Spring       (JPA)
                             Data JPA)
```

con una capa de DTOs para no exponer las entidades directamente, y un filtro
JWT que intercepta las requests antes de llegar a los controllers.

## Funcionalidades

- [x] Registro / login de usuario (JWT)
- [x] CRUD de tareas
- [x] Categorías
- [x] Prioridad y fecha límite
- [x] Tablero Kanban con drag & drop (persistente)
- [x] Filtros por categoría, estado y búsqueda por texto

## Cómo correrlo localmente

### Backend

Requiere PostgreSQL corriendo en `localhost:5432` con una base `gestor_tareas`
ya creada.

```bash
cd backend
DB_PASSWORD=tu_password ./mvnw spring-boot:run
```

Variables de entorno disponibles (todas opcionales, con default para desarrollo):

| Variable | Default |
|---|---|
| `DB_NAME` | `gestor_tareas` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | _(vacío)_ |
| `JWT_SECRET` | valor de desarrollo, **cambiar en producción** |
| `CORS_ALLOWED_ORIGIN` | `http://localhost:5173` |

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Por defecto apunta a `http://localhost:8080`. Para cambiarlo, definir
`VITE_API_URL` en un `.env` dentro de `frontend/`.

## Endpoints de la API

```
POST   /api/auth/registro
POST   /api/auth/login
GET    /api/categorias
POST   /api/categorias
DELETE /api/categorias/{id}
GET    /api/tareas?estado=&categoriaId=&texto=
POST   /api/tareas
PUT    /api/tareas/{id}
PATCH  /api/tareas/{id}/mover
DELETE /api/tareas/{id}
```

Todos los endpoints (excepto `/api/auth/**`) requieren un header
`Authorization: Bearer <token>` y devuelven solo los datos del usuario
autenticado.
