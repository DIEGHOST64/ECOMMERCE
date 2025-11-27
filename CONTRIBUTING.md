# E-Commerce Platform - Contributing Guide

## 🎯 Cómo Contribuir

¡Gracias por tu interés en contribuir a este proyecto! Este documento te guiará en el proceso.

## 📋 Antes de Empezar

1. Familiarízate con la arquitectura del proyecto leyendo el `README.md`
2. Asegúrate de que tu entorno de desarrollo esté configurado correctamente
3. Revisa los issues abiertos para ver si alguien más está trabajando en algo similar

## 🔧 Configuración del Entorno

1. Haz fork del repositorio
2. Clona tu fork localmente:
   ```cmd
   git clone https://github.com/TU_USUARIO/EECCOMERCE-802.git
   ```
3. Configura el repositorio upstream:
   ```cmd
   git remote add upstream https://github.com/DIEGHOST64/EECCOMERCE-802.git
   ```
4. Sigue las instrucciones de `INSTALL.md` para configurar el proyecto

## 🌿 Flujo de Trabajo con Git

1. Crea una nueva rama para tu feature:
   ```cmd
   git checkout -b feature/nombre-descriptivo
   ```
   
2. Realiza tus cambios y haz commits descriptivos:
   ```cmd
   git add .
   git commit -m "feat: descripción clara del cambio"
   ```

3. Mantén tu rama actualizada con upstream:
   ```cmd
   git fetch upstream
   git rebase upstream/main
   ```

4. Push a tu fork:
   ```cmd
   git push origin feature/nombre-descriptivo
   ```

5. Crea un Pull Request desde GitHub

## 📝 Estándares de Código

### Java/Spring Boot
- Sigue las convenciones de Clean Architecture
- Usa Lombok para reducir boilerplate
- Documenta métodos públicos con Javadoc
- Nombres de clases en PascalCase, métodos en camelCase

### React/JavaScript
- Usa componentes funcionales con hooks
- Nombres de componentes en PascalCase
- Usa nombres descriptivos para variables y funciones
- Formatea con Prettier (si está configurado)

### Commits
Usa Conventional Commits:
- `feat:` nueva funcionalidad
- `fix:` corrección de bug
- `docs:` cambios en documentación
- `refactor:` refactorización sin cambios funcionales
- `test:` agregar o modificar tests
- `chore:` cambios en build, dependencias, etc.

## 🧪 Testing

- Asegúrate de que todos los tests pasen antes de hacer PR
- Agrega tests para nuevas funcionalidades
- Tests unitarios en cada servicio

## 📖 Documentación

- Actualiza el README.md si cambias funcionalidad principal
- Documenta nuevas variables de entorno en .env.example
- Comenta código complejo o no obvio

## 🐛 Reportar Bugs

Usa el template de issues de GitHub e incluye:
- Descripción del problema
- Pasos para reproducir
- Comportamiento esperado vs actual
- Versiones (Java, Node, PostgreSQL, etc.)
- Logs relevantes

## ✨ Solicitar Features

- Explica el caso de uso claramente
- Describe el comportamiento esperado
- Considera alternativas si las hay

## 📬 Contacto

Para preguntas o discusiones, abre un issue o contacta al mantenedor.

## 📄 Licencia

Al contribuir, aceptas que tu código se licencie bajo la misma licencia que el proyecto.

---

¡Gracias por contribuir! 🎉
