# E-Commerce Frontend

Frontend moderno y elegante para el sistema de e-commerce desarrollado con React.

## 🎨 Características

- ✨ Diseño moderno con gradientes y animaciones
- 🔐 Sistema de autenticación (Login/Registro)
- 📦 Catálogo de productos con diseño atractivo
- 🛒 Carrito de compras funcional
- 📱 Diseño responsive (móvil y desktop)
- 🎯 Navegación intuitiva

## 📋 Prerrequisitos

Antes de comenzar, asegúrate de tener instalado:

1. **Node.js** (v14 o superior)
   - Descarga desde: https://nodejs.org/
   - Verifica la instalación: `node --version`

2. **Microservicios Backend** corriendo en:
   - Auth: http://localhost:9090
   - Catalogo: http://localhost:9092
   - Notificaciones: http://localhost:9093

## 🚀 Instalación

1. Abre una terminal en la carpeta `frontend`:
```cmd
cd "c:\Users\PC\Documents\Proyecto ECCOMERCE\Proyecto ECCOMERCE\Proyecto ECCOMERCE\frontend"
```

2. Instala las dependencias:
```cmd
npm install
```

## ▶️ Ejecutar el Frontend

```cmd
npm start
```

El frontend se abrirá automáticamente en: **http://localhost:3000**

## 📁 Estructura del Proyecto

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── components/
│   │   ├── Navbar.js/css       # Barra de navegación
│   │   ├── Login.js            # Página de inicio de sesión
│   │   ├── Register.js         # Página de registro
│   │   ├── ProductList.js/css  # Listado de productos
│   │   ├── Cart.js/css         # Carrito de compras
│   │   └── Auth.css            # Estilos de autenticación
│   ├── App.js                  # Componente principal
│   ├── App.css                 # Estilos globales de la app
│   ├── index.js                # Punto de entrada
│   └── index.css               # Estilos base
└── package.json
```

## 🎯 Funcionalidades

### Autenticación
- **Registro**: Crear nueva cuenta de usuario
- **Login**: Iniciar sesión con email y contraseña
- **Logout**: Cerrar sesión

### Productos
- Ver catálogo completo de productos
- Información detallada (nombre, precio, stock)
- Agregar productos al carrito

### Carrito
- Ver productos en el carrito
- Eliminar productos del carrito
- Ver total de la compra
- Realizar compra

## 🎨 Tecnologías Utilizadas

- **React 18** - Framework de UI
- **React Router DOM** - Navegación
- **Axios** - Peticiones HTTP
- **CSS3** - Estilos con gradientes y animaciones

## 🔧 Configuración

El frontend está configurado para conectarse a los microservicios en:
- Auth API: `http://localhost:9090`
- Catalog API: `http://localhost:9092`

Si necesitas cambiar estos endpoints, modifica las URLs en los componentes correspondientes.

## 📝 Notas

- El token de autenticación se guarda en `localStorage`
- El contador del carrito se actualiza automáticamente
- Las rutas están protegidas (requieren autenticación)

## 🐛 Solución de Problemas

### Error: "Cannot connect to backend"
- Verifica que los microservicios estén corriendo
- Revisa que los puertos 9090 y 9092 estén disponibles

### Error: "npm not found"
- Instala Node.js desde https://nodejs.org/
- Reinicia la terminal después de la instalación

### Error en dependencias
```cmd
rm -rf node_modules package-lock.json
npm install
```

## 🎉 ¡Listo!

Tu frontend está configurado y listo para usar. Disfruta de tu e-commerce!
