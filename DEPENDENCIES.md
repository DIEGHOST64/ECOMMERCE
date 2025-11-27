# Dependencias del Proyecto E-Commerce

## 📦 Resumen de Dependencias

Este documento lista todas las dependencias utilizadas en el proyecto para referencia rápida.

## Backend - Microservicios Java/Spring Boot

### Auth Service

**Framework Base:**
- Spring Boot 3.5.5
- Java 17

**Dependencias Maven:**
```xml
<!-- Spring Boot Starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
    <version>6.5.5</version>
</dependency>

<!-- Base de Datos -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Utilidades -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Variables de Entorno -->
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>4.0.0</version>
</dependency>

<!-- Mapeo de Objetos -->
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>3.1.0</version>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Catálogo Service

**Framework Base:**
- Spring Boot 3.5.7
- Java 17

**Dependencias Maven:**
```xml
<!-- Spring Boot Starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Base de Datos -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Utilidades -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Variables de Entorno -->
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>4.0.0</version>
</dependency>

<!-- AWS SQS para mensajería -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>sqs</artifactId>
    <version>2.20.26</version>
</dependency>

<!-- Jackson para manejo de fechas -->
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Notificaciones Service

**Framework Base:**
- Spring Boot 3.5.5
- Java 17
- AWS SDK 2.20.26

**Dependencias Maven:**
```xml
<!-- Spring Boot Starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- AWS SDK BOM (Bill of Materials) -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>bom</artifactId>
    <version>2.20.26</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>

<!-- AWS SQS -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>sqs</artifactId>
</dependency>

<!-- AWS SES (Simple Email Service) -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>ses</artifactId>
</dependency>

<!-- Twilio para SMS -->
<dependency>
    <groupId>com.twilio.sdk</groupId>
    <artifactId>twilio</artifactId>
    <version>9.14.1</version>
</dependency>

<!-- Variables de Entorno -->
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>4.0.0</version>
</dependency>

<!-- Utilidades -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Jackson -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

## Frontend - React

**Runtime:**
- Node.js 16+
- npm o yarn

**Dependencias npm:**
```json
{
  "dependencies": {
    "axios": "^1.6.2",           // Cliente HTTP
    "react": "^18.2.0",          // Framework React
    "react-dom": "^18.2.0",      // React DOM
    "react-icons": "^5.5.0",     // Biblioteca de iconos
    "react-router-dom": "^6.20.0", // Enrutamiento
    "react-scripts": "5.0.1"     // Scripts de Create React App
  }
}
```

## 🗄️ Base de Datos

**PostgreSQL:**
- Versión recomendada: 12+
- Dos bases de datos:
  - `ecommerce_auth` (para Auth Service)
  - `ecommerce` (para Catálogo Service)

## ☁️ Servicios Externos (Opcionales)

**AWS:**
- SQS (Simple Queue Service) - Mensajería asíncrona
- SES (Simple Email Service) - Envío de correos

**Twilio:**
- API de SMS para notificaciones

## 🛠️ Herramientas de Desarrollo

**Obligatorias:**
- JDK 17+ (OpenJDK o Oracle)
- Maven 3.6+ (incluido Maven Wrapper en el proyecto)
- Node.js 16+ y npm
- PostgreSQL 12+

**Opcionales:**
- Git
- IDE (IntelliJ IDEA, VS Code, Eclipse)
- Postman o Insomnia (para probar APIs)
- pgAdmin (cliente PostgreSQL)

## 📊 Comandos de Instalación Rápida

### Backend (cada microservicio)
```cmd
mvnw.cmd clean install
```

### Frontend
```cmd
npm install
```

## 🔄 Actualizar Dependencias

### Maven
```cmd
mvnw.cmd versions:display-dependency-updates
```

### npm
```cmd
npm outdated
npm update
```

## 📝 Notas Importantes

1. **Maven Wrapper**: El proyecto incluye `mvnw.cmd`, no necesitas instalar Maven globalmente
2. **Variables de Entorno**: Todas las credenciales se gestionan mediante archivos `.env`
3. **Spring Dotenv**: Permite usar archivos `.env` en Spring Boot
4. **AWS SDK**: Solo necesario si vas a usar las funcionalidades de notificaciones
5. **Twilio**: Solo necesario para SMS, el email funciona con AWS SES

## 🔒 Seguridad

- Nunca subas archivos `.env` a Git
- Usa `.env.example` como plantilla
- Las dependencias se actualizan regularmente para parches de seguridad
- Revisa vulnerabilidades con:
  ```cmd
  mvnw.cmd dependency-check:check
  npm audit
  ```

---

Para más información, consulta el `README.md` principal.
