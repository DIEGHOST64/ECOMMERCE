# Dependencias

Todas las dependencias son open source. Ya no se usan SDK de AWS, Twilio, ModelMapper, Axios ni `gh-pages`.

## Backend

### Común a los tres servicios

- **Java 17**
- **Spring Boot 3.5.7** (parent)
- **Maven Wrapper** (`mvnw` / `mvnw.cmd`)
- **Lombok** (gestionado por Spring Boot)
- **Spring Boot Starter Test**
- **Spring Boot Maven Plugin**

### Auth (`auth/auth/auth/pom.xml`)

| Dependencia | Uso |
| --- | --- |
| `spring-boot-starter-web` | API REST |
| `spring-boot-starter-data-jpa` | Persistencia |
| `spring-boot-starter-security` | Autenticación y autorización |
| `spring-boot-starter-validation` | Validación de DTOs |
| `spring-boot-starter-amqp` | Publicación en RabbitMQ |
| `spring-boot-starter-actuator` | Healthcheck |
| `postgresql` (runtime) | Driver PostgreSQL |
| `jjwt-api`, `jjwt-impl`, `jjwt-jackson` 0.12.6 | Generación y validación de JWT |
| `spring-security-test` (test) | Pruebas de seguridad |
| `h2` (test) | Base de datos en memoria para tests |

### Catálogo (`catalogo/catalogo/pom.xml`)

| Dependencia | Uso |
| --- | --- |
| `spring-boot-starter-web` | API REST |
| `spring-boot-starter-data-jpa` | Persistencia |
| `spring-boot-starter-security` | Validación de JWT y roles |
| `spring-boot-starter-validation` | Validación de DTOs |
| `spring-boot-starter-amqp` | Publicación en RabbitMQ |
| `spring-boot-starter-actuator` | Healthcheck |
| `postgresql` (runtime) | Driver PostgreSQL |
| `jjwt-api`, `jjwt-impl`, `jjwt-jackson` 0.12.6 | Validación de JWT |
| `spring-security-test` (test) | Pruebas de seguridad |
| `h2` (test) | Base de datos en memoria para tests |

### Notificaciones (`notificaciones/pom.xml`)

| Dependencia | Uso |
| --- | --- |
| `spring-boot-starter-web` | Servidor HTTP y actuator |
| `spring-boot-starter-amqp` | Consumo de RabbitMQ |
| `spring-boot-starter-mail` | Envío SMTP |
| `spring-boot-starter-actuator` | Healthcheck |
| `spring-rabbit-test` (test) | Pruebas del listener RabbitMQ |

### Dependencias retiradas

- `software.amazon.awssdk:sqs` y `ses` (AWS).
- `com.twilio.sdk:twilio`.
- `org.modelmapper:modelmapper`.
- `me.paulschwarz:spring-dotenv` (la configuración se inyecta por variables de entorno).
- `spring-security-web` declarado a mano (lo aporta `spring-boot-starter-security`).

## Frontend (`frontend/package.json`)

| Dependencia | Versión | Uso |
| --- | --- | --- |
| `react` | ^18.2.0 | UI |
| `react-dom` | ^18.2.0 | Renderizado |
| `react-icons` | ^5.5.0 | Iconos |
| `react-router-dom` | ^6.20.0 | Rutas |
| `react-scripts` | 5.0.1 | Create React App (build, test, dev server) |

Dependencias retiradas:

- `axios`: se usa `fetch` nativo a través de `src/api/client.js`.
- `gh-pages`: el despliegue ya no es GitHub Pages.

El archivo `package-lock.json` se versiona para que `npm ci` sea reproducible en CI y en Docker.

## Infraestructura

| Imagen | Versión | Uso |
| --- | --- | --- |
| `postgres` | `16-alpine` | Base de datos |
| `rabbitmq` | `3.13-management-alpine` | Mensajería y consola |
| `axllent/mailpit` | `latest` | SMTP de desarrollo y visor de correos |
| `maven` | `3.9-eclipse-temurin-17` | Build de los servicios |
| `eclipse-temurin` | `17-jre-alpine` | Runtime de los servicios |
| `node` | `20-alpine` | Build del frontend |
| `nginx` | `1.27-alpine` | Servidor estático del frontend |

## Herramientas de desarrollo

Obligatorias:

- Docker con Compose v2.
- Git.

Opcionales:

- JDK 17 y Node.js 20 para desarrollo sin contenedores.
- Postman o Insomnia.
- `psql` o pgAdmin.
- `openssl` para generar secretos.

## Actualizar dependencias

### Maven

```bash
cd auth/auth/auth
./mvnw versions:display-dependency-updates
```

Repite en Catálogo y Notificaciones. Actualiza versiones de forma controlada y ejecuta `./mvnw clean verify`.

### npm

```bash
cd frontend
npm outdated
npm update
npm test -- --watchAll=false
npm run build
```

### Vulnerabilidades

```bash
cd frontend
npm audit

# Backend: requiere el plugin OWASP Dependency-Check si se añade al pom
./mvnw org.owasp:dependency-check-maven:check
```

## Notas

- Los servicios no comparten librerías Java; el contrato de notificaciones es JSON y se documenta en [ARCHITECTURE.md](./ARCHITECTURE.md#5-mensajería).
- `JWT_SECRET` es la única configuración que debe coincidir obligatoriamente entre Auth y Catálogo.
- Mailpit y el usuario `guest` de RabbitMQ son solo para desarrollo.
