# Frontend

Aplicación React 18 de la plataforma E-Commerce. Consume Auth (9090) y Catálogo (9092) con JWT.

## Requisitos

- Node.js 20 y npm.
- Auth y Catálogo en ejecución (ver [README.md](../README.md) o [INSTALL.md](../INSTALL.md)).

## Instalación

```bash
cd frontend
npm ci
npm start
```

Abre http://localhost:3000.

## Configuración

Las URLs se leen de variables de entorno en tiempo de compilación:

```env
REACT_APP_AUTH_URL=http://localhost:9090
REACT_APP_CATALOGO_URL=http://localhost:9092
REACT_APP_REQUEST_TIMEOUT_MS=15000
```

Puedes definirlas en `frontend/.env.local` (ignorado por Git) o en el entorno antes de `npm start`/`npm run build`.

En Docker, las URLs se pasan como build args desde el `.env` raíz. Si cambias una URL, reconstruye el frontend:

```bash
docker compose build frontend
docker compose up -d frontend
```

## Estructura

```text
frontend/
├── src/
│   ├── api/
│   │   ├── client.js       Cliente fetch, JWT, timeout y errores
│   │   ├── authApi.js      Login, registro y perfil
│   │   └── catalogoApi.js  Productos, carrito y compras
│   ├── components/
│   │   ├── Login.js
│   │   ├── Register.js
│   │   ├── ProductList.js
│   │   ├── Cart.js
│   │   ├── Orders.js
│   │   ├── AdminPanel.js
│   │   └── Navbar.js
│   ├── session.js          Sesión JWT en localStorage
│   ├── config.js           URLs y timeout
│   ├── App.js              Rutas públicas y protegidas
│   └── index.js
├── nginx.conf              Servido estático en Docker
├── Dockerfile
└── package.json
```

## Sesión y roles

- La sesión se guarda en `localStorage` bajo `ecommerce.session`.
- Contiene `token`, `tokenType`, `expiresAt` y `user` (`id`, `nombre`, `email`, `role`).
- `session.js` normaliza `ADMIN`/`ROLE_ADMIN` a `ADMIN` y cualquier otro rol a `USER`.
- Si el token expiró, la sesión se elimina al leerla.
- Si el backend responde `401` con un token enviado, se limpia la sesión y se emite `session:expired`; `App` redirige al login.
- No se envían `X-User-Role` ni `usuarioId`; el backend deriva la identidad del JWT.

## Rutas

| Ruta | Acceso |
| --- | --- |
| `/login`, `/register` | Público (redirige si ya hay sesión) |
| `/productos` | Usuario autenticado |
| `/carrito` | Usuario autenticado |
| `/compras` | Usuario autenticado |
| `/admin` | Solo `ADMIN` |
| `/` | Redirige según el rol |

## Pruebas y build

```bash
npm test -- --watchAll=false
npm run build
```

Resultado esperado: 12 tests y build sin warnings.

## Solución de problemas

### El navegador no conecta con el backend

- Verifica que Auth y Catálogo estén arriba y que `CORS_ALLOWED_ORIGINS` incluya `http://localhost:3000`.
- Revisa la consola del navegador y la pestaña Network.

### `401` inmediato

- El token expiró o `JWT_SECRET` no coincide entre Auth y Catálogo.
- Vuelve a iniciar sesión después de corregir la configuración.

### `403` en el panel de administración

El usuario no tiene rol `ADMIN`. Promuévelo en PostgreSQL y vuelve a iniciar sesión.

### El frontend usa URLs equivocadas

Las variables `REACT_APP_*` se compilan en el bundle. Reinicia el servidor de desarrollo o reconstruye la imagen después de cambiarlas.
