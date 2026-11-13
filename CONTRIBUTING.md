# Contribución

Gracias por contribuir. Este documento describe el entorno, las reglas de arquitectura y el flujo de trabajo.

## 1. Configuración del entorno

1. Haz fork del repositorio.
2. Clona tu fork:

```bash
git clone https://github.com/TU_USUARIO/ECOMMERCE.git
cd ECOMMERCE
```

3. Añade el repositorio original como upstream:

```bash
git remote add upstream https://github.com/DIEGHOST64/ECOMMERCE.git
```

4. Crea `.env` desde `.env.example` y edita los secretos.
5. Levanta la plataforma:

```bash
docker compose up -d --build
```

6. Consulta [INSTALL.md](./INSTALL.md) para el desarrollo local con Maven/npm y [ARCHITECTURE.md](./ARCHITECTURE.md) para entender las capas.

## 2. Flujo de Git

1. Actualiza tu rama principal:

```bash
git checkout main
git pull upstream main
```

2. Crea una rama descriptiva:

```bash
git checkout -b feature/nombre-descriptivo
# o fix/, refactor/, docs/, test/, chore/
```

3. Haz commits pequeños y con intención.
4. Sincroniza con upstream:

```bash
git fetch upstream
git rebase upstream/main
```

5. Publica en tu fork:

```bash
git push origin feature/nombre-descriptivo
```

6. Abre un Pull Request contra `main` y describe el cambio, las pruebas ejecutadas y cualquier decisión relevante.

## 3. Convención de commits

Usa Conventional Commits:

- `feat:` nueva funcionalidad.
- `fix:` corrección de un bug.
- `refactor:` cambio interno sin cambiar el comportamiento.
- `test:` pruebas nuevas o ajustadas.
- `docs:` documentación.
- `chore:` build, dependencias, CI.
- `perf:` mejora de rendimiento.
- `security:` corrección de seguridad.

Ejemplos:

```text
feat: agregar validacion de stock en el checkout
fix: corregir expiracion de sesion en el frontend
refactor: mover el adaptador de RabbitMQ a infraestructura
```

## 4. Reglas de arquitectura

Cada servicio backend respeta la regla de dependencia:

```text
infraestructura -> application -> domain
```

- **domain**
  - Modelos, excepciones y puertos.
  - No importar Spring, JPA, Jackson ni RabbitMQ.
  - No incluir lógica de infraestructura (SQL, HTTP, plantillas de correo).
- **application**
  - Implementa los puertos de entrada.
  - Depende solo de puertos de salida y del dominio.
  - No conoce controllers, repositorios JPA ni adaptadores concretos.
- **infraestructura**
  - Controllers, DTOs, mappers, repositorios, seguridad, mensajería y configuración.
  - Implementa los puertos de salida.
  - El wiring de casos de uso va en `UseCaseConfig`.

Reglas adicionales:

- No exponer entidades de dominio directamente en la API; usar DTOs.
- No poner reglas de negocio en controllers.
- No acceder desde un servicio a las tablas de otro.
- La identidad se toma del JWT; no aceptar `usuarioId` ni roles del cliente.
- Los nuevos puertos deben tener su implementación en `infraestructura` y su prueba.
- Los mensajes de RabbitMQ son JSON y no deben depender de clases Java compartidas.

## 5. Estilo de código

### Java

- Java 17.
- Nombres en español coherentes con el dominio existente (`Usuario`, `Carrito`, `Compra`).
- Clases en PascalCase, métodos y variables en camelCase.
- Lombok para getters/setters/constructores cuando aporte claridad.
- Javadoc en puertos y en lógica no obvia.
- Preferir `record` para DTOs inmutables.

### React/JavaScript

- Componentes funcionales y hooks.
- Nombres de componentes en PascalCase.
- Lógica HTTP en `src/api/`; la sesión en `src/session.js`.
- No hardcodear URLs, roles ni tokens.
- Manejar errores de red y mostrar mensajes al usuario.

### Formato

- 4 espacios en Java.
- 2 espacios en JavaScript/JSON/YAML.
- Una línea en blanco al final de cada archivo.
- Evitar código comentado y `console.log` de depuración.

## 6. Pruebas

Antes de abrir un PR:

```bash
# Backend
cd auth/auth/auth && ./mvnw clean verify
cd catalogo/catalogo && ./mvnw clean verify
cd notificaciones && ./mvnw clean verify

# Frontend
cd frontend
npm ci
npm test -- --watchAll=false
npm run build
```

En Windows:

```powershell
cd auth\auth\auth
cmd /c "mvnw.cmd clean verify"

cd ..\..\..\catalogo\catalogo
cmd /c "mvnw.cmd clean verify"

cd ..\..\notificaciones
cmd /c "mvnw.cmd clean verify"

cd ..\..\frontend
npm.cmd test -- --watchAll=false
npm.cmd run build
```

Reglas:

- Toda funcionalidad nueva debe incluir pruebas.
- No reducir la cobertura existente.
- Los tests no deben depender de servicios externos reales: usa H2 y mocks.
- Si cambias el contrato de la API o de RabbitMQ, actualiza [API.md](./API.md) y [ARCHITECTURE.md](./ARCHITECTURE.md).

## 7. Documentación

- Actualiza `README.md` si cambia el arranque o la arquitectura.
- Documenta nuevas variables en `.env.example`.
- Actualiza `API.md` si cambian endpoints, DTOs o códigos de error.
- Actualiza `CHANGELOG.md` para cambios visibles.
- No incluyas secretos, tokens ni contraseñas en ejemplos.

## 8. Seguridad

- Nunca hagas commit de `.env`, claves, tokens ni certificados.
- No reutilices credenciales de ejemplos o de versiones anteriores.
- Si detectas un secreto en el historial, avisa al mantenedor y rótalo; no lo publiques en un issue.
- No desactives CSRF, CORS o las reglas de autorización sin justificarlo en el PR.
- No aceptes roles ni identidades enviadas por el cliente.
- Reporta vulnerabilidades de forma privada al mantenedor.

## 9. Checklist del Pull Request

- [ ] El cambio está en una rama descriptiva.
- [ ] Los tests de los servicios afectados pasan.
- [ ] El build del frontend pasa sin warnings.
- [ ] No hay secretos ni `.env` en el diff.
- [ ] La arquitectura por capas se respeta.
- [ ] La documentación y los `.env.example` están actualizados.
- [ ] El PR explica el qué, el por qué y cómo probarlo.

## 10. Licencia

Al contribuir aceptas que tu código se publique bajo la licencia del proyecto. Consulta [LICENSE](./LICENSE).
