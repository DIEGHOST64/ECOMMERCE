# ✅ Checklist antes de Subir a GitHub

Sigue esta lista para asegurarte de que tu proyecto está listo para ser compartido en GitHub.

## 📋 Verificaciones Previas

### 1. Archivos Sensibles
- [ ] Verificar que **NO** hay archivos `.env` con credenciales reales
- [ ] Confirmar que `.gitignore` incluye `*.env`
- [ ] Verificar que `.env.example` está presente en cada servicio
- [ ] Remover cualquier API key o contraseña en el código fuente

### 2. Archivos de Configuración
- [ ] `.gitignore` está configurado correctamente
- [ ] Archivos `.env.example` están completos y documentados
- [ ] `README.md` principal está actualizado
- [ ] `LICENSE` está presente (si aplica)

### 3. Documentación
- [ ] `README.md` explica cómo instalar y ejecutar el proyecto
- [ ] `INSTALL.md` tiene instrucciones paso a paso
- [ ] `API.md` documenta los endpoints (si aplica)
- [ ] `DEPENDENCIES.md` lista todas las dependencias
- [ ] Comentarios importantes en el código están en español/inglés

### 4. Código
- [ ] No hay código comentado innecesario
- [ ] No hay `console.log()` o `System.out.println()` de debugging
- [ ] No hay archivos de prueba temporales (test.js, prueba.java, etc.)
- [ ] El código compila sin errores

### 5. Dependencias
- [ ] `pom.xml` de cada servicio está actualizado
- [ ] `package.json` del frontend está actualizado
- [ ] No hay dependencias sin usar

### 6. Base de Datos
- [ ] Scripts SQL están documentados (si aplica)
- [ ] No hay credenciales hardcodeadas en los scripts
- [ ] Instrucciones de creación de BD están en el README

## 🚀 Pasos para Subir a GitHub

### 1. Preparar el Repositorio Local

```cmd
cd "c:\Users\PC\Documents\Proyecto ECCOMERCE\Proyecto ECCOMERCE\Proyecto ECCOMERCE"

# Verificar estado de Git
git status

# Ver qué archivos se subirán
git add --dry-run .
```

### 2. Revisar .gitignore

Asegúrate de que `.gitignore` incluye:

```
# Variables de entorno
*.env
!.env.example

# Compilados
target/
node_modules/
build/
dist/

# IDEs
.idea/
.vscode/
*.iml

# Base de datos local
*.db
*.sqlite
```

### 3. Crear Commits Organizados

```cmd
# Inicializar Git (si no está inicializado)
git init

# Agregar archivos por categorías
git add README.md LICENSE .gitignore
git commit -m "docs: add project documentation and configuration"

git add pom.xml */pom.xml package.json
git commit -m "build: add dependency configuration files"

git add auth/ catalogo/ notificaciones/ frontend/
git commit -m "feat: add microservices implementation"

git add *.md
git commit -m "docs: add comprehensive documentation"
```

### 4. Crear Repositorio en GitHub

1. Ve a https://github.com/new
2. Nombre: `EECCOMERCE-802` (o el que prefieras)
3. Descripción: `E-Commerce platform with microservices architecture`
4. Visibilidad: Público o Privado
5. **NO** inicialices con README (ya lo tienes)
6. Click en "Create repository"

### 5. Conectar y Subir

```cmd
# Agregar remote
git remote add origin https://github.com/DIEGHOST64/EECCOMERCE-802.git

# Renombrar rama a main (si es necesario)
git branch -M main

# Subir a GitHub
git push -u origin main
```

### 6. Verificar en GitHub

- [ ] Todos los archivos están presentes
- [ ] No hay archivos `.env` visibles
- [ ] El README.md se muestra correctamente
- [ ] Los archivos `.env.example` están presentes

## 🔒 Seguridad - Última Verificación

Ejecuta estos comandos para buscar posibles problemas:

```cmd
# Buscar archivos .env
dir /s /b *.env

# Buscar posibles API keys en el código
findstr /s /i "api_key password secret token" *.java *.js *.properties
```

Si encuentras algo, **NO LO SUBAS** y elimínalo primero.

## 📝 Después de Subir

### 1. Configurar GitHub
- [ ] Agregar descripción del repositorio
- [ ] Agregar topics/tags (java, spring-boot, react, microservices, ecommerce)
- [ ] Habilitar Issues (para que otros reporten bugs)
- [ ] Habilitar Discussions (opcional)

### 2. Agregar Badge al README (Opcional)

```markdown
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green)
![React](https://img.shields.io/badge/React-18-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-blue)
```

### 3. Crear Release (Opcional)

Si quieres marcar una versión:
1. Ve a "Releases" en GitHub
2. Click en "Create a new release"
3. Tag: `v1.0.0`
4. Título: "Initial Release"
5. Descripción: Resumen de funcionalidades

## 🎯 Mejores Prácticas

### Para Commits Futuros

```cmd
# Bueno
git commit -m "feat: add user authentication"
git commit -m "fix: resolve cart total calculation bug"
git commit -m "docs: update installation instructions"

# Malo
git commit -m "changes"
git commit -m "update"
git commit -m "fix bug"
```

### Convención de Commits

- `feat:` nueva funcionalidad
- `fix:` corrección de bug
- `docs:` cambios en documentación
- `style:` formato, espacios, etc.
- `refactor:` refactorización de código
- `test:` agregar tests
- `chore:` tareas de mantenimiento

## ⚠️ Errores Comunes a Evitar

- ❌ Subir archivos `.env` con credenciales reales
- ❌ Subir carpetas `node_modules/` o `target/`
- ❌ Subir archivos de configuración del IDE (`.idea/`, `.vscode/`)
- ❌ Commits con mensaje "asdf" o "test"
- ❌ Subir archivos de bases de datos locales
- ❌ Dejar contraseñas en el código

## 🆘 Si Subiste Algo Sensible por Error

```cmd
# NUNCA hagas esto si ya hiciste push público
# Contacta a GitHub Support inmediatamente

# Para repositorios privados o antes del push:
git reset --soft HEAD~1  # Deshacer último commit
git reset HEAD archivo   # Quitar archivo del staging
```

## 📞 Ayuda

Si tienes dudas:
- GitHub Docs: https://docs.github.com/
- Git Basics: https://git-scm.com/book/en/v2

---

Una vez completado este checklist, ¡tu proyecto estará listo para GitHub! 🚀
