# Checklist antes de publicar en GitHub

Repositorio: `https://github.com/DIEGHOST64/ECOMMERCE`
Rama de trabajo: `main`

> **Estado de seguridad:** el historial público fue purgado el 2026-11-12. El repositorio anterior quedó archivado como `ECOMMERCE-archive` (privado) y debe eliminarse. Las credenciales de AWS/Twilio que estuvieron expuestas deben rotarse o revocarse igualmente.

## 1. Archivos sensibles

- [ ] No hay `.env` ni variantes (`*.env`, `.env.local`) en el staging.
- [ ] `.gitignore` ignora `.env` y permite `!.env.example` y `!**/.env.example`.
- [ ] Los `.env.example` solo contienen marcadores, nunca credenciales reales.
- [ ] No hay claves de AWS, Twilio, SMTP ni JWT en scripts, propiedades, tests o documentación.
- [ ] No hay logs de build, `target/`, `node_modules/` ni `frontend/build/` versionados.

Comandos de verificación (PowerShell):

```powershell
git status --short
git ls-files | Select-String -Pattern '\.env$|\.env\.|build-.*\.log'
git grep -n -E 'AKIA[0-9A-Z]{16}|TWILIO_AUTH_TOKEN=[A-Za-z0-9]{20,}|AWS_SECRET_ACCESS_KEY=[A-Za-z0-9+/]{20,}'
```

Si alguna búsqueda devuelve resultados en archivos versionados, detente y elimínalos antes de continuar.

## 2. Calidad y pruebas

- [ ] `auth`: `mvnw clean verify` en verde (19 tests).
- [ ] `catalogo`: `mvnw clean verify` en verde (20 tests).
- [ ] `notificaciones`: `mvnw clean verify` en verde (16 tests).
- [ ] Frontend: `npm test -- --watchAll=false` en verde (12 tests).
- [ ] Frontend: `npm run build` compila sin warnings.
- [ ] `docker compose config -q` es válido con un `.env` de ejemplo.
- [ ] El workflow de CI pasa en la rama.

## 3. Documentación

- [ ] `README.md` describe el arranque con Docker y el stack open source.
- [ ] `ARCHITECTURE.md` refleja las capas y la mensajería actuales.
- [ ] `API.md` documenta los endpoints reales con JWT.
- [ ] `INSTALL.md` cubre Docker y desarrollo local.
- [ ] `DEPLOYMENT.md` cubre producción, TLS, backups y rotación de secretos.
- [ ] `DEPENDENCIES.md` no menciona AWS, Twilio, Axios ni ModelMapper.
- [ ] `POSTMAN_COLLECTION.json` es JSON válido y usa Bearer.
- [ ] `CONTRIBUTING.md` apunta a `ECOMMERCE`, no a nombres antiguos.
- [ ] `CHANGELOG.md` no contiene contraseñas ni secretos.

## 4. Configuración del repositorio

- [ ] `.gitignore` cubre Java, Node, IDEs, logs, bases locales y entornos.
- [ ] `package-lock.json` está versionado.
- [ ] `docker-compose.yml` usa variables de entorno y healthchecks.
- [ ] Los Dockerfiles usan multi-stage y usuario no root.
- [ ] `.github/workflows/ci.yml` está presente.
- [ ] `LICENSE` está presente.

## 5. Revisar el diff

```powershell
git diff --stat
git diff --cached --stat
git status
```

Revisa especialmente:

- Archivos nuevos en `auth/`, `catalogo/`, `notificaciones/` y `frontend/src/`.
- Cambios en `application.properties` y `.env.example`.
- Eliminación de configuraciones de Netlify/Render/GitHub Pages.
- Cualquier línea que parezca una credencial.

## 6. Commit y push

```powershell
git add -A
git commit -m "refactor: migrar a stack open source y Clean Architecture"

git push -u origin main
```

Si el push falla por autenticación:

1. Instala GitHub CLI (`gh`) o configura un Personal Access Token con permiso `repo`.
2. Autentica:

```powershell
gh auth login
# o
git config --global credential.helper manager
```

3. Repite el push.

`main` ya está publicada. Para cambios futuros, usa una rama y un PR; no hagas push directo a `main` hasta que la CI esté en verde.

## 7. Historial de Git

El historial público ya fue purgado. Si vuelve a ocurrir una exposición, **rota las credenciales primero** y luego reescribe el historial; el purge no invalida las claves.

```bash
# git filter-repo
git filter-repo --replace-text replacements.txt
git push --force --all
git push --force --tags

# BFG
java -jar bfg.jar --replace-text replacements.txt
git reflog expire --expire=now --all
git gc --prune=now --aggressive
git push --force
```

`replacements.txt` debe contener los valores exactos a reemplazar por `REDACTED`. No lo subas al repositorio.

Después del force-push:

- [ ] Los colaboradores vuelven a clonar o hacen `git fetch --all && git reset --hard origin/main`.
- [ ] Se invalida cualquier fork o copia que conserve los commits antiguos.
- [ ] Se revisan los logs de AWS CloudTrail y Twilio para detectar uso no autorizado.

## 8. Después de publicar

- [ ] Descripción del repositorio: plataforma de e-commerce con microservicios Spring Boot, React, PostgreSQL y RabbitMQ.
- [ ] Topics: `java`, `spring-boot`, `react`, `microservices`, `ecommerce`, `rabbitmq`, `postgresql`, `docker`, `clean-architecture`.
- [ ] Issues habilitados.
- [ ] Protección de la rama `main`: PR obligatorio y CI en verde.
- [ ] Secret scanning y Dependabot habilitados.
- [ ] Release `v2.0.0` con el resumen de `CHANGELOG.md`.

## 9. Errores comunes

- ❌ Subir `.env` o un `.env.example` con credenciales reales.
- ❌ Versionar `target/`, `node_modules/` o `build/`.
- ❌ Hacer commit de logs de build.
- ❌ Mantener `render.yaml`, `netlify.toml` o enlaces a GitHub Pages.
- ❌ Publicar la rama sin ejecutar las pruebas.
- ❌ Hacer force-push sin avisar a los colaboradores.
- ❌ Creer que borrar un secreto del `HEAD` lo elimina del historial.
