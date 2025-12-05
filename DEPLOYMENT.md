# 🚀 Guía de Despliegue - E-Commerce Microservicios

Esta guía te ayudará a desplegar el proyecto completo de forma gratuita.

## 📋 Tabla de Contenidos

- [Frontend en GitHub Pages](#frontend-en-github-pages)
- [Frontend en Netlify](#frontend-en-netlify-alternativa)
- [Backend en Render](#backend-en-render)
- [Backend en Railway](#backend-en-railway-alternativa)
- [Base de Datos](#base-de-datos)

---

## 🎨 Frontend en GitHub Pages

### Paso 1: Configuración
El proyecto ya está configurado. Solo necesitas:

```bash
cd frontend
npm install gh-pages --save-dev
npm run deploy
```

### Paso 2: Habilitar GitHub Pages
1. Ve a tu repositorio en GitHub
2. Settings → Pages
3. Source: Selecciona "gh-pages" branch
4. Save

Tu frontend estará disponible en: `https://dieghost64.github.io/ECOMMERCE`

---

## 🎨 Frontend en Netlify (Alternativa)

### Opción 1: Despliegue Directo

1. Ve a [netlify.com](https://netlify.com) e inicia sesión con GitHub
2. Click en "New site from Git"
3. Selecciona el repositorio ECOMMERCE
4. Configuración:
   - **Build command:** `cd frontend && npm run build`
   - **Publish directory:** `frontend/build`
5. Click "Deploy site"

### Opción 2: Con Netlify CLI

```bash
# Instalar Netlify CLI
npm install -g netlify-cli

# Login
netlify login

# Desplegar
cd frontend
npm run build
netlify deploy --prod
```

---

## 🔧 Backend en Render

Render ofrece servicios gratuitos para PostgreSQL y Spring Boot.

### Paso 1: Crear Base de Datos PostgreSQL

1. Ve a [render.com](https://render.com) y crea una cuenta
2. Dashboard → New → PostgreSQL
3. Configuración:
   - **Name:** ecommerce-db
   - **Database:** ecommerce
   - **User:** postgres
   - **Region:** selecciona la más cercana
   - **Plan:** Free
4. Click "Create Database"
5. **Guarda la URL de conexión externa**

### Paso 2: Desplegar Servicio Auth

1. Dashboard → New → Web Service
2. Conecta tu repositorio de GitHub
3. Configuración:
   - **Name:** ecommerce-auth
   - **Environment:** Java
   - **Build Command:** `cd auth/auth/auth && ./mvnw clean package -DskipTests`
   - **Start Command:** `cd auth/auth/auth && java -jar target/auth-0.0.1-SNAPSHOT.jar`
   - **Plan:** Free
4. Variables de entorno:
   ```
   DB_HOST=<tu-host-render-postgresql>
   DB_PORT=5432
   DB_NAME=ecommerce
   DB_USERNAME=postgres
   DB_PASSWORD=<tu-password-render>
   JWT_SECRET=tu_clave_secreta_jwt_muy_larga_y_segura
   ```
5. Click "Create Web Service"

### Paso 3: Desplegar Servicio Catálogo

1. Dashboard → New → Web Service
2. Repite el proceso con:
   - **Name:** ecommerce-catalogo
   - **Build Command:** `cd catalogo/catalogo && ./mvnw clean package -DskipTests`
   - **Start Command:** `cd catalogo/catalogo && java -jar target/catalogo-0.0.1-SNAPSHOT.jar`
3. Mismas variables de entorno + agregar:
   ```
   AUTH_SERVICE_URL=https://ecommerce-auth.onrender.com
   ```

### Paso 4: Actualizar Frontend

Edita las URLs del backend en tu código frontend para apuntar a:
- Auth: `https://ecommerce-auth.onrender.com`
- Catálogo: `https://ecommerce-catalogo.onrender.com`

---

## 🚂 Backend en Railway (Alternativa)

Railway también ofrece despliegue gratuito.

### Configuración Rápida

1. Ve a [railway.app](https://railway.app) e inicia sesión con GitHub
2. New Project → Deploy from GitHub repo
3. Selecciona ECOMMERCE
4. Railway detectará automáticamente los servicios
5. Agrega una base de datos PostgreSQL desde el dashboard
6. Configura las variables de entorno automáticamente

**Nota:** Railway tiene un límite de 500 horas/mes en el plan gratuito.

---

## 💾 Base de Datos

### Opciones Gratuitas

1. **Render PostgreSQL** (Recomendado)
   - 256 MB RAM
   - 1 GB almacenamiento
   - Ideal para desarrollo

2. **Neon** (Alternativa)
   - 10 GB almacenamiento
   - Serverless PostgreSQL
   - [neon.tech](https://neon.tech)

3. **ElephantSQL**
   - 20 MB gratis
   - PostgreSQL como servicio
   - [elephantsql.com](https://elephantsql.com)

---

## 🔐 Seguridad en Producción

Antes de desplegar, asegúrate de:

1. ✅ Cambiar `JWT_SECRET` a un valor seguro y único
2. ✅ Usar contraseñas fuertes para la base de datos
3. ✅ Habilitar HTTPS (incluido en Render/Netlify)
4. ✅ Configurar CORS correctamente para tus dominios de producción
5. ✅ No subir archivos `.env` al repositorio

---

## 📊 Monitoreo

- **Render:** Incluye logs y métricas básicas
- **Netlify:** Analytics disponible
- **Alternativa:** [UptimeRobot](https://uptimerobot.com) para monitorear disponibilidad

---

## 🆘 Solución de Problemas

### El frontend no se conecta al backend
- Verifica que las URLs en el código frontend sean correctas
- Asegúrate de que CORS esté configurado en el backend

### El backend no inicia
- Revisa los logs en Render/Railway
- Verifica las variables de entorno
- Asegúrate de que la base de datos esté accesible

### Base de datos no conecta
- Verifica el formato de la URL de conexión
- Confirma que el firewall permita conexiones externas
- Revisa usuario y contraseña

---

## ✅ Checklist de Despliegue

- [ ] Base de datos PostgreSQL creada
- [ ] Variables de entorno configuradas
- [ ] Servicio Auth desplegado
- [ ] Servicio Catálogo desplegado
- [ ] Frontend actualizado con URLs de producción
- [ ] Frontend desplegado
- [ ] CORS configurado
- [ ] Probado registro de usuario
- [ ] Probado login
- [ ] Probado creación de productos
- [ ] Probado carrito y compras

---

## 📚 Recursos Adicionales

- [Documentación de Render](https://render.com/docs)
- [Documentación de Netlify](https://docs.netlify.com)
- [Spring Boot en producción](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html)

---

**¡Feliz despliegue! 🎉**
