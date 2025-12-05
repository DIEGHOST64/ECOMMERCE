# 🚀 Guía Paso a Paso - Despliegue en Netlify + Render

## ⏱️ Tiempo estimado: 20-30 minutos

---

## 📋 Parte 1: Crear Cuentas (5 min)

### 1. Crear cuenta en Render
1. Ve a [render.com](https://render.com)
2. Click en **"Get Started"**
3. Regístrate con tu cuenta de GitHub
4. Autoriza el acceso a tus repositorios

### 2. Crear cuenta en Netlify
1. Ve a [netlify.com](https://netlify.com)
2. Click en **"Sign up"**
3. Regístrate con tu cuenta de GitHub
4. Autoriza el acceso

✅ **Checkpoint:** Tienes ambas cuentas activas

---

## 💾 Parte 2: Base de Datos PostgreSQL en Render (5 min)

1. En Render Dashboard, click **"New +"** → **"PostgreSQL"**

2. Configuración:
   ```
   Name: ecommerce-db
   Database: ecommerce
   User: postgres
   Region: Oregon (US West) o Frankfurt (Europe)
   PostgreSQL Version: 16
   Plan: Free
   ```

3. Click **"Create Database"**

4. **IMPORTANTE:** Guarda estos datos (aparecen en la página de la BD):
   ```
   Internal Database URL: postgresql://...
   External Database URL: postgresql://...
   Host: dpg-xxxxx.oregon-postgres.render.com
   Port: 5432
   Database: ecommerce
   Username: postgres
   Password: [tu_password_generado]
   ```

5. Espera 2-3 minutos hasta que el estado sea **"Available"** (punto verde)

✅ **Checkpoint:** Base de datos PostgreSQL creada y activa

---

## 🔧 Parte 3: Desplegar Auth Service en Render (5 min)

1. En Render Dashboard, click **"New +"** → **"Web Service"**

2. Click **"Build and deploy from a Git repository"**

3. Conecta tu repositorio:
   - Si no aparece, click **"Configure account"** y autoriza acceso
   - Selecciona el repositorio **"ECOMMERCE"**

4. Configuración del servicio:
   ```
   Name: ecommerce-auth
   Region: Same as database (Oregon o Frankfurt)
   Branch: main
   Runtime: Java
   Build Command: cd auth/auth/auth && chmod +x mvnw && ./mvnw clean package -DskipTests
   Start Command: cd auth/auth/auth && java -Dserver.port=$PORT -jar target/auth-0.0.1-SNAPSHOT.jar
   Plan: Free
   ```

5. **Variables de Entorno** (click "Advanced" → "Add Environment Variable"):
   ```
   DB_HOST = [el host de tu BD sin puerto, ej: dpg-xxxxx.oregon-postgres.render.com]
   DB_PORT = 5432
   DB_NAME = ecommerce
   DB_USERNAME = postgres
   DB_PASSWORD = [tu password de la BD]
   JWT_SECRET = mi_super_secreto_jwt_key_12345_muy_seguro
   AWS_REGION = us-east-1
   AWS_ACCESS_KEY_ID = (vacío por ahora)
   AWS_SECRET_ACCESS_KEY = (vacío por ahora)
   AWS_SQS_QUEUE_URL = (vacío por ahora)
   ```

6. Click **"Create Web Service"**

7. Espera 5-10 minutos mientras compila y despliega
   - Verás logs en tiempo real
   - El estado cambiará a **"Live"** cuando esté listo

8. **GUARDA LA URL:** `https://ecommerce-auth.onrender.com`

✅ **Checkpoint:** Auth Service desplegado y funcionando

---

## 🛍️ Parte 4: Desplegar Catálogo Service en Render (5 min)

1. En Render Dashboard, click **"New +"** → **"Web Service"**

2. Selecciona el mismo repositorio **"ECOMMERCE"**

3. Configuración:
   ```
   Name: ecommerce-catalogo
   Region: Same as database
   Branch: main
   Runtime: Java
   Build Command: cd catalogo/catalogo && chmod +x mvnw && ./mvnw clean package -DskipTests
   Start Command: cd catalogo/catalogo && java -Dserver.port=$PORT -jar target/catalogo-0.0.1-SNAPSHOT.jar
   Plan: Free
   ```

4. **Variables de Entorno:**
   ```
   DB_HOST = [mismo que auth]
   DB_PORT = 5432
   DB_NAME = ecommerce
   DB_USERNAME = postgres
   DB_PASSWORD = [mismo que auth]
   AUTH_SERVICE_URL = https://ecommerce-auth.onrender.com
   AWS_REGION = us-east-1
   AWS_ACCESS_KEY_ID = (vacío)
   AWS_SECRET_ACCESS_KEY = (vacío)
   AWS_SQS_QUEUE_URL = (vacío)
   ```

5. Click **"Create Web Service"**

6. Espera 5-10 minutos

7. **GUARDA LA URL:** `https://ecommerce-catalogo.onrender.com`

✅ **Checkpoint:** Ambos microservicios desplegados

---

## 🧪 Parte 5: Probar Backend (2 min)

Abre Postman o tu navegador:

1. **Probar Auth:**
   ```
   GET https://ecommerce-auth.onrender.com/api/ecommerce/usuario/1
   ```

2. **Probar Catálogo:**
   ```
   GET https://ecommerce-catalogo.onrender.com/api/ecommerce/producto
   ```

Si ves errores 404 o respuestas JSON, ¡funciona! ✅

---

## 🎨 Parte 6: Actualizar Frontend con URLs de Producción (3 min)

Necesitas crear un archivo de configuración para las URLs del backend.

**En tu computadora local:**

1. Abre el proyecto en VS Code

2. Crea `frontend/src/config.js`:
   ```javascript
   const config = {
     AUTH_URL: process.env.REACT_APP_AUTH_URL || 'http://localhost:9090',
     CATALOGO_URL: process.env.REACT_APP_CATALOGO_URL || 'http://localhost:9092'
   };
   
   export default config;
   ```

3. Actualiza los archivos que hacen peticiones HTTP para usar estas URLs

4. Guarda cambios y haz commit:
   ```bash
   git add .
   git commit -m "feat: Configuracion de URLs para produccion"
   git push origin main
   ```

✅ **Checkpoint:** Frontend preparado para producción

---

## 🌐 Parte 7: Desplegar Frontend en Netlify (5 min)

### Opción A: Deploy desde Git (Recomendado)

1. En Netlify Dashboard, click **"Add new site"** → **"Import an existing project"**

2. Selecciona **"Deploy with GitHub"**

3. Busca y selecciona tu repositorio **"ECOMMERCE"**

4. Configuración:
   ```
   Site name: ecommerce-frontend-[tu-nombre] (o deja auto-generar)
   Branch to deploy: main
   Base directory: frontend
   Build command: npm run build
   Publish directory: frontend/build
   ```

5. **Environment variables:**
   ```
   REACT_APP_AUTH_URL = https://ecommerce-auth.onrender.com
   REACT_APP_CATALOGO_URL = https://ecommerce-catalogo.onrender.com
   ```

6. Click **"Deploy site"**

7. Espera 2-3 minutos

8. **GUARDA LA URL:** `https://[nombre-random].netlify.app`

### Opción B: Deploy manual con CLI

```bash
cd frontend
npm install netlify-cli -g
netlify login
npm run build
netlify deploy --prod
```

✅ **Checkpoint:** Frontend desplegado

---

## 🔧 Parte 8: Configurar CORS en Backend (Importante!)

Los backends necesitan permitir peticiones desde tu dominio de Netlify.

**Ya está configurado en tu código, pero verifica que las URLs coincidan:**

En ambos microservicios, el CORS debe incluir tu URL de Netlify.

---

## ✅ Parte 9: Prueba Final

1. Abre tu sitio de Netlify: `https://[tu-sitio].netlify.app`

2. Prueba el flujo completo:
   - ✅ Registro de usuario
   - ✅ Login
   - ✅ Ver productos
   - ✅ Agregar al carrito
   - ✅ Realizar compra

---

## 🎉 ¡FELICIDADES!

Tu proyecto está desplegado y funcionando en producción:

- 🎨 Frontend: `https://[tu-sitio].netlify.app`
- 🔐 Auth API: `https://ecommerce-auth.onrender.com`
- 🛍️ Catálogo API: `https://ecommerce-catalogo.onrender.com`
- 💾 Base de Datos: Render PostgreSQL

---

## 📝 URLs para tu Hoja de Vida

```
Demo en vivo: https://[tu-sitio].netlify.app
Código fuente: https://github.com/DIEGHOST64/ECOMMERCE
Documentación API: https://github.com/DIEGHOST64/ECOMMERCE/blob/main/API.md
```

---

## ⚠️ Notas Importantes

1. **Servicios gratuitos de Render se duermen** después de 15 min sin uso
   - Primera petición puede tardar 30-60 segundos en despertar
   - Es normal, es parte del plan gratuito

2. **Límites del plan Free:**
   - Render: 750 horas/mes (suficiente)
   - Netlify: 100GB bandwidth/mes
   - PostgreSQL: 256MB RAM, 1GB storage

3. **Para mantener el servicio activo:**
   - Usa un servicio como [UptimeRobot](https://uptimerobot.com) para hacer ping cada 5 minutos

---

## 🆘 Solución de Problemas

### Error: Build failed
- Verifica que las rutas en Build Command sean correctas
- Revisa los logs en Render/Netlify

### Error: Cannot connect to database
- Verifica que las variables de entorno estén correctas
- Copia exactamente el password de PostgreSQL

### Frontend no carga
- Verifica las variables de entorno en Netlify
- Revisa la consola del navegador (F12)

### CORS Error
- Asegúrate que la URL de Netlify esté en la configuración CORS del backend
- Verifica que las URLs en el frontend sean correctas (https, no http)

---

**¿Necesitas ayuda?** Revisa los logs en Render/Netlify o consulta la documentación.
