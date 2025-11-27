# E-Commerce Microservices Platform

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green?style=for-the-badge&logo=springboot)
![React](https://img.shields.io/badge/React-18-blue?style=for-the-badge&logo=react)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-blue?style=for-the-badge&logo=postgresql)
![AWS](https://img.shields.io/badge/AWS-SQS%20%7C%20SES-orange?style=for-the-badge&logo=amazonaws)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

Sistema de comercio electrónico desarrollado con arquitectura de microservicios utilizando Spring Boot, React y AWS.

## 📋 Tabla de Contenidos

- [Descripción](#descripción)
- [Características Principales](#características-principales)
- [Inicio Rápido](#inicio-rápido)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Prerrequisitos](#prerrequisitos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Ejecución](#ejecución)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Documentación Adicional](#documentación-adicional)
- [Contribuciones](#contribuciones)
- [Licencia](#licencia)

## 🚀 Inicio Rápido

```bash
# 1. Clonar el repositorio
git clone https://github.com/DIEGHOST64/EECCOMERCE-802.git
cd EECCOMERCE-802

# 2. Crear bases de datos PostgreSQL
psql -U postgres -c "CREATE DATABASE ecommerce_auth;"
psql -U postgres -c "CREATE DATABASE ecommerce;"

# 3. Configurar variables de entorno
copy auth\auth\auth\.env.example auth\auth\auth\.env
copy catalogo\catalogo\.env.example catalogo\catalogo\.env
# Editar archivos .env con tus credenciales

# 4. Instalar dependencias frontend
cd frontend
npm install
cd ..

# 5. Ejecutar servicios (PowerShell)
.\start-services.ps1

# O manualmente en terminales separadas:
# Terminal 1: cd auth\auth\auth && mvnw.cmd spring-boot:run
# Terminal 2: cd catalogo\catalogo && mvnw.cmd spring-boot:run
# Terminal 3: cd frontend && npm start
```

Accede a http://localhost:3000 🎉

## 📝 Descripción

Plataforma de e-commerce modular construida con microservicios que incluye:
- ✅ Sistema de autenticación y autorización
- 🛍️ Catálogo de productos y gestión de carrito
- 📧 Sistema de notificaciones (Email/SMS)
- 🎨 Interfaz de usuario React responsiva

## ✨ Características Principales

- **Arquitectura de Microservicios**: Servicios independientes y escalables
- **Clean Architecture**: Código organizado y mantenible
- **API RESTful**: Endpoints bien documentados
- **Autenticación JWT**: Seguridad robusta
- **Mensajería Asíncrona**: Procesamiento en background con AWS SQS
- **Responsive Design**: Interfaz adaptable a dispositivos móviles
- **Base de Datos Relacional**: PostgreSQL para persistencia de datos
- **Notificaciones Multi-canal**: Email (AWS SES) y SMS (Twilio)

## 🏗️ Arquitectura

El proyecto está compuesto por los siguientes microservicios:

- **Auth Service** (Puerto 9090): Gestión de usuarios, autenticación y autorización
- **Catálogo Service** (Puerto 9092): Gestión de productos, categorías y carrito de compras
- **Notificaciones Service** (Puerto 9093): Envío de notificaciones por email (AWS SES) y SMS (Twilio)
- **Frontend** (Puerto 3000): Aplicación React para la interfaz de usuario

## 🛠️ Tecnologías

### Backend
- **Java 17**
- **Spring Boot 3.5.x**
- **Spring Data JPA**
- **PostgreSQL** - Base de datos
- **Maven** - Gestión de dependencias
- **AWS SDK** (SQS, SES) - Mensajería y correo electrónico
- **Twilio API** - SMS
- **Lombok** - Reducción de código boilerplate
- **Spring Dotenv** - Gestión de variables de entorno

### Frontend
- **React 18**
- **React Router DOM 6**
- **Axios** - Cliente HTTP
- **React Icons**

## ✅ Prerrequisitos

Asegúrate de tener instalado:

### 1. Java Development Kit (JDK)
- **Versión requerida**: JDK 17 o superior
- **Verificar instalación**:
  ```cmd
  java -version
  ```
- **Descargar**: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) o [OpenJDK](https://adoptium.net/)

### 2. PostgreSQL
- **Versión requerida**: PostgreSQL 12 o superior
- **Verificar instalación**:
  ```cmd
  psql --version
  ```
- **Descargar**: [PostgreSQL](https://www.postgresql.org/download/windows/)

### 3. Node.js y npm
- **Versión requerida**: Node.js 16 o superior
- **Verificar instalación**:
  ```cmd
  node --version
  npm --version
  ```
- **Descargar**: [Node.js](https://nodejs.org/)

### 4. Maven (Opcional)
- El proyecto incluye Maven Wrapper (`mvnw`), por lo que no es necesario instalar Maven globalmente
- Si prefieres usar Maven global: [Apache Maven](https://maven.apache.org/download.cgi)

## 📦 Instalación

### 1. Clonar el Repositorio

```cmd
git clone https://github.com/DIEGHOST64/EECCOMERCE-802.git
cd EECCOMERCE-802
```

### 2. Configurar Base de Datos PostgreSQL

**Iniciar sesión en PostgreSQL**:
```cmd
psql -U postgres
```

**Crear las bases de datos**:
```sql
CREATE DATABASE ecommerce_auth;
CREATE DATABASE ecommerce;
\q
```

### 3. Instalar Dependencias del Frontend

```cmd
cd frontend
npm install
cd ..
```

## ⚙️ Configuración

### Configuración de Microservicios

Cada microservicio requiere su archivo de configuración. Debes crear archivos `.env` en cada servicio con las credenciales correspondientes.

#### Auth Service

Crear archivo `.env` en `auth/auth/auth/.env`:
```properties
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/ecommerce_auth
DB_USERNAME=postgres
DB_PASSWORD=tu_password_postgresql

# Server Configuration
SERVER_PORT=9090
```

#### Catálogo Service

Crear archivo `.env` en `catalogo/catalogo/.env`:
```properties
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/ecommerce
DB_USERNAME=postgres
DB_PASSWORD=tu_password_postgresql

# Server Configuration
SERVER_PORT=9092

# AWS SQS Configuration (Opcional - solo si usas notificaciones)
AWS_ACCESS_KEY_ID=tu_aws_access_key
AWS_SECRET_ACCESS_KEY=tu_aws_secret_key
AWS_REGION=us-east-1
SQS_QUEUE_URL=https://sqs.us-east-1.amazonaws.com/tu-cuenta/nombre-cola
```

#### Notificaciones Service

Crear archivo `.env` en `notificaciones/.env`:
```properties
# AWS SES Configuration
AWS_ACCESS_KEY_ID=tu_aws_access_key
AWS_SECRET_ACCESS_KEY=tu_aws_secret_key
AWS_REGION=us-east-1
SES_SENDER_EMAIL=tu-email-verificado@ejemplo.com

# AWS SQS Configuration
SQS_QUEUE_URL=https://sqs.us-east-1.amazonaws.com/tu-cuenta/nombre-cola

# Twilio Configuration (para SMS)
TWILIO_ACCOUNT_SID=tu_twilio_account_sid
TWILIO_AUTH_TOKEN=tu_twilio_auth_token
TWILIO_PHONE_NUMBER=+1234567890

# Server Configuration
SERVER_PORT=9093
```

**Nota**: El microservicio de notificaciones es opcional. Si no deseas usar AWS SES o Twilio, puedes omitir este servicio.

### Archivos de Ejemplo

Se han incluido archivos `.env.example` en cada servicio como referencia. Cópialos y renómbralos a `.env`:

```cmd
copy catalogo\catalogo\.env.example catalogo\catalogo\.env
copy auth\auth\auth\.env.example auth\auth\auth\.env
copy notificaciones\.env.example notificaciones\.env
```

## 🚀 Ejecución

### Opción 1: Inicio Automático con Scripts (Windows)

**Iniciar todos los servicios**:
```cmd
.\start-services.ps1
```

**Detener todos los servicios**:
```cmd
.\stop-services.ps1
```

### Opción 2: Inicio Manual

#### 1. Iniciar Auth Service
```cmd
cd auth\auth\auth
mvnw.cmd spring-boot:run
```

#### 2. Iniciar Catálogo Service (en otra terminal)
```cmd
cd catalogo\catalogo
mvnw.cmd spring-boot:run
```

#### 3. Iniciar Notificaciones Service (Opcional - en otra terminal)
```cmd
cd notificaciones
mvnw.cmd spring-boot:run
```

#### 4. Iniciar Frontend (en otra terminal)
```cmd
cd frontend
npm start
```

### Verificar que los Servicios Están Corriendo

- **Auth Service**: http://localhost:9090
- **Catálogo Service**: http://localhost:9092
- **Notificaciones Service**: http://localhost:9093
- **Frontend**: http://localhost:3000

## 📁 Estructura del Proyecto

```
EECCOMERCE-802/
│
├── auth/auth/auth/                  # Microservicio de Autenticación
│   ├── src/
│   │   ├── main/java/com/ecommerce/auth/
│   │   │   ├── application/         # Casos de uso
│   │   │   ├── domain/              # Entidades y lógica de negocio
│   │   │   └── infrastructure/      # Controladores, repositorios, config
│   │   └── resources/
│   │       └── application.properties
│   ├── pom.xml
│   └── .env.example
│
├── catalogo/catalogo/               # Microservicio de Catálogo
│   ├── src/
│   │   ├── main/java/com/ecommerce/catalogo/
│   │   │   ├── application/         # Casos de uso
│   │   │   ├── domain/              # Entidades y lógica de negocio
│   │   │   └── infraestructure/     # Controladores, repositorios, config
│   │   └── resources/
│   │       └── application.properties
│   ├── pom.xml
│   └── .env.example
│
├── notificaciones/                  # Microservicio de Notificaciones
│   ├── src/
│   │   ├── main/java/com/ecommerce/notificaciones/
│   │   └── resources/
│   │       └── application.properties
│   ├── pom.xml
│   └── .env.example
│
├── frontend/                        # Aplicación React
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   │   ├── Login.js
│   │   │   ├── Register.js
│   │   │   ├── ProductList.js
│   │   │   ├── Cart.js
│   │   │   ├── Orders.js
│   │   │   └── AdminPanel.js
│   │   ├── App.js
│   │   └── index.js
│   └── package.json
│
├── README.md                        # Este archivo
└── .gitignore
```

## 🗄️ Base de Datos

### Migraciones Iniciales

El proyecto incluye scripts SQL para configurar la base de datos:

```cmd
# Agregar columna de imagen (si es necesario)
psql -U postgres -d ecommerce -f add-imagen-column.sql
```

Las tablas se crean automáticamente al iniciar los servicios gracias a JPA (Hibernate).

## 🔑 Variables de Entorno

Las variables de entorno se gestionan mediante archivos `.env` en cada microservicio:

### Variables Obligatorias
- `DB_URL`: URL de conexión a PostgreSQL
- `DB_USERNAME`: Usuario de PostgreSQL
- `DB_PASSWORD`: Contraseña de PostgreSQL

### Variables Opcionales (Notificaciones)
- `AWS_ACCESS_KEY_ID`: Credenciales de AWS
- `AWS_SECRET_ACCESS_KEY`: Credenciales de AWS
- `AWS_REGION`: Región de AWS
- `SQS_QUEUE_URL`: URL de la cola SQS
- `SES_SENDER_EMAIL`: Email verificado en AWS SES
- `TWILIO_ACCOUNT_SID`: Credenciales de Twilio
- `TWILIO_AUTH_TOKEN`: Credenciales de Twilio
- `TWILIO_PHONE_NUMBER`: Número de teléfono de Twilio

## 🐛 Solución de Problemas

### Error: "Cannot connect to database"
- Verifica que PostgreSQL esté corriendo
- Confirma que las credenciales en `.env` sean correctas
- Verifica que las bases de datos `ecommerce_auth` y `ecommerce` existan

### Error: "Port already in use"
- Asegúrate de que los puertos 9090, 9092, 9093 y 3000 estén disponibles
- Cierra cualquier aplicación que esté usando esos puertos

### Error: "JAVA_HOME not set"
- Configura la variable de entorno JAVA_HOME apuntando a tu instalación de JDK
- Ejemplo: `JAVA_HOME=C:\Program Files\Java\jdk-17`

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

## 📚 Documentación Adicional

- 📖 [**INSTALL.md**](INSTALL.md) - Guía de instalación rápida
- 🏗️ [**ARCHITECTURE.md**](ARCHITECTURE.md) - Diagrama y explicación de la arquitectura
- 🌐 [**API.md**](API.md) - Documentación completa de endpoints
- 📦 [**DEPENDENCIES.md**](DEPENDENCIES.md) - Lista detallada de dependencias
- ✅ [**GITHUB_CHECKLIST.md**](GITHUB_CHECKLIST.md) - Checklist para publicar en GitHub
- 🤝 [**CONTRIBUTING.md**](CONTRIBUTING.md) - Guía para contribuir al proyecto

## 👥 Autor

**DIEGHOST64**
- GitHub: [@DIEGHOST64](https://github.com/DIEGHOST64)

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Haz fork del proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

Lee [CONTRIBUTING.md](CONTRIBUTING.md) para más detalles sobre el proceso de contribución.

## ⭐ Apoyo

Si este proyecto te fue útil, considera darle una estrella en GitHub ⭐

## 📞 Soporte

Si encuentras algún problema o tienes preguntas:
- Abre un [Issue](https://github.com/DIEGHOST64/EECCOMERCE-802/issues) en GitHub
- Revisa la documentación en la carpeta `/docs`
- Consulta el archivo [INSTALL.md](INSTALL.md) para problemas de instalación

---

**Construido con ❤️ usando Spring Boot y React**
#   U p d a t e d   2 0 2 5 - 1 1 - 2 6   2 0 : 2 0 : 2 3 
 
 
