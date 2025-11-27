# E-Commerce Microservices Platform

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green?style=for-the-badge&logo=springboot)
![React](https://img.shields.io/badge/React-18-blue?style=for-the-badge&logo=react)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-blue?style=for-the-badge&logo=postgresql)
![AWS](https://img.shields.io/badge/AWS-SQS%20%7C%20SES-orange?style=for-the-badge&logo=amazonaws)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

Sistema de comercio electronico desarrollado con arquitectura de microservicios utilizando Spring Boot, React y AWS.

## Tabla de Contenidos

- [Descripcion](#descripcion)
- [Caracteristicas Principales](#caracteristicas-principales)
- [Inicio Rapido](#inicio-rapido)
- [Arquitectura](#arquitectura)
- [Tecnologias](#tecnologias)
- [Prerrequisitos](#prerrequisitos)
- [Instalacion](#instalacion)
- [Configuracion](#configuracion)
- [Ejecucion](#ejecucion)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Documentacion Adicional](#documentacion-adicional)
- [Contribuciones](#contribuciones)
- [Licencia](#licencia)

## Inicio Rapido

```bash
# 1. Clonar el repositorio
git clone https://github.com/DIEGHOST64/ECOMMERCE.git
cd ECOMMERCE

# 2. Crear bases de datos PostgreSQL
psql -U postgres -c "CREATE DATABASE ecommerce_auth;"
psql -U postgres -c "CREATE DATABASE ecommerce;"

# 3. Configurar variables de entorno
copy auth\auth\auth\.env.example auth\auth\auth\.env
copy catalogo\catalogo\.env.example catalogo\catalogo\.env

# 4. Instalar dependencias frontend
cd frontend
npm install
cd ..

# 5. Ejecutar servicios (PowerShell)
.\start-services.ps1
```

Accede a http://localhost:3000

## Descripcion

Plataforma de e-commerce modular construida con microservicios que incluye:
- Sistema de autenticacion y autorizacion
- Catalogo de productos y gestion de carrito
- Sistema de notificaciones (Email/SMS)
- Interfaz de usuario React responsiva

## Caracteristicas Principales

**Arquitectura de Microservicios**: Servicios independientes y escalables
**Clean Architecture**: Codigo organizado y mantenible
**API RESTful**: Endpoints bien documentados
**Autenticacion JWT**: Seguridad robusta
**Mensajeria Asincrona**: Procesamiento en background con AWS SQS
**Responsive Design**: Interfaz adaptable a dispositivos moviles
**Base de Datos Relacional**: PostgreSQL para persistencia de datos
**Notificaciones Multi-canal**: Email (AWS SES) y SMS (Twilio)

## Arquitectura

El proyecto esta compuesto por los siguientes microservicios:

**Auth Service** (Puerto 9090): Gestion de usuarios, autenticacion y autorizacion
**Catalogo Service** (Puerto 9092): Gestion de productos, categorias y carrito de compras
**Notificaciones Service** (Puerto 9093): Envio de notificaciones por email (AWS SES) y SMS (Twilio)
**Frontend** (Puerto 3000): Aplicacion React para la interfaz de usuario

## Tecnologias

### Backend
- **Java 17**
- **Spring Boot 3.5.x**
- **Spring Data JPA**
- **PostgreSQL**
- **Maven**
- **AWS SDK** (SQS, SES)
- **Twilio API**

### Frontend
- **React 18**
- **Vite**
- **Tailwind CSS**
- **Axios**

## Prerrequisitos

- Java 17 o superior
- Node.js 18 o superior
- PostgreSQL 12 o superior
- Maven 3.8 o superior
- Cuenta AWS (para SQS y SES)
- Cuenta Twilio (para SMS)

## Instalacion

1. Clonar el repositorio:
```bash
git clone https://github.com/DIEGHOST64/ECOMMERCE.git
cd ECOMMERCE
```

2. Configurar bases de datos:
```sql
CREATE DATABASE ecommerce_auth;
CREATE DATABASE ecommerce;
```

3. Configurar variables de entorno en cada servicio

4. Instalar dependencias:
```bash
# Frontend
cd frontend
npm install
```

## Configuracion

Editar archivos de configuracion con tus credenciales:
- `auth/auth/auth/src/main/resources/application.properties`
- `catalogo/catalogo/src/main/resources/application.properties`
- `notificaciones/src/main/resources/application.properties`

## Ejecucion

### PowerShell
```powershell
.\start-services.ps1
```

### Manual
```bash
# Terminal 1: Auth Service
cd auth\auth\auth
mvnw.cmd spring-boot:run

# Terminal 2: Catalogo Service
cd catalogo\catalogo
mvnw.cmd spring-boot:run

# Terminal 3: Notificaciones Service
cd notificaciones
mvnw.cmd spring-boot:run

# Terminal 4: Frontend
cd frontend
npm start
```

## Estructura del Proyecto

```
ECOMMERCE/
 auth/                    # Servicio de autenticacion
 catalogo/                # Servicio de catalogo
 notificaciones/          # Servicio de notificaciones
 frontend/                # Aplicacion React
 start-services.ps1       # Script para iniciar servicios
 README.md
```

## Documentacion Adicional

- [Arquitectura](./ARCHITECTURE.md)
- [API Documentation](./API.md)
- [Guia de Contribucion](./CONTRIBUTING.md)
- [Dependencias](./DEPENDENCIES.md)

## Contribuciones

Las contribuciones son bienvenidas. Por favor:
1. Fork el proyecto
2. Crea una rama para tu feature
3. Commit tus cambios
4. Push a la rama
5. Abre un Pull Request

## Licencia

Este proyecto esta bajo la Licencia MIT. Ver el archivo `LICENSE` para mas detalles.

---

Desarrollado con dedicacion por el equipo de desarrollo