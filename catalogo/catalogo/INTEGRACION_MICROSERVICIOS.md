# Integración entre Microservicios - Catálogo y Auth

## Problema Identificado

El microservicio de **catálogo** estaba funcionando de forma **independiente** cuando debería comunicarse con el microservicio de **auth** para validar usuarios.

### Estado Anterior (Incorrecto):
- ❌ El microservicio de catálogo tenía su propia tabla `usuario` en la base de datos
- ❌ Usaba un repositorio JPA (`UsuarioDataJpaRepository`) para consultar usuarios localmente
- ❌ No se comunicaba con el microservicio de autenticación
- ❌ Violaba el principio de responsabilidad única de microservicios

### Estado Actual (Correcto):
- ✅ El microservicio de catálogo se comunica con el microservicio de auth mediante HTTP
- ✅ No tiene tabla de usuarios propia
- ✅ Usa `RestTemplate` para validar usuarios a través del servicio de autenticación
- ✅ Implementa correctamente la arquitectura de microservicios

## Cambios Realizados

### 1. Configuración (`application.properties`)
```properties
# Cambio de nombre de la aplicación (estaba "auth", ahora "catalogo")
spring.application.name=catalogo

# Nueva configuración: URL del microservicio de autenticación
auth.service.url=http://localhost:9090
```

### 2. `UsuarioGatewayImpl.java`

**Antes:**
- Inyectaba `UsuarioDataJpaRepository`
- Consultaba la base de datos local con `existsById()`

**Ahora:**
- Inyecta `RestTemplate` para hacer llamadas HTTP
- Usa `@Value` para obtener la URL del servicio de auth desde la configuración
- Hace una llamada GET a: `http://localhost:9090/api/usuarios/{id}/existe`
- Maneja excepciones de comunicación apropiadamente

```java
@Override
public boolean usuarioExiste(Long usuarioId) {
    if (usuarioId == null || usuarioId <= 0) {
        return false;
    }

    try {
        String url = authServiceUrl + "/api/usuarios/" + usuarioId + "/existe";
        ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return Boolean.TRUE.equals(response.getBody());
        }
        return false;
    } catch (Exception e) {
        System.err.println("Error al verificar usuario con ID " + usuarioId + ": " + e.getMessage());
        return false;
    }
}
```

### 3. Archivos Eliminados
- ❌ `UsuarioData.java` - Entidad JPA eliminada
- ❌ `UsuarioDataJpaRepository.java` - Repositorio JPA eliminado
- ❌ Directorio `/usuario` completo

## Requisitos para que Funcione

### En el Microservicio de Auth (puerto 9090)

Debe existir un endpoint que el microservicio de catálogo pueda consumir:

```java
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    
    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> usuarioExiste(@PathVariable Long id) {
        boolean existe = usuarioService.existePorId(id);
        return ResponseEntity.ok(existe);
    }
}
```

### Configuración de Puertos
- **Microservicio Auth:** Puerto 9090
- **Microservicio Catálogo:** Puerto 9092

### Base de Datos
- El microservicio de **auth** maneja la tabla `usuario`
- El microservicio de **catálogo** ya NO tiene tabla de usuarios

## Flujo de Validación de Usuario

1. Un cliente hace una petición al microservicio de catálogo (ej: agregar producto al carrito)
2. El microservicio de catálogo necesita validar que el usuario existe
3. **Catálogo** hace una llamada HTTP a **Auth**: `GET http://localhost:9090/api/usuarios/{id}/existe`
4. **Auth** consulta su base de datos y responde `true` o `false`
5. **Catálogo** continúa o rechaza la operación según la respuesta

## Ventajas de esta Arquitectura

✅ **Separación de responsabilidades:** Cada microservicio maneja su dominio
✅ **Escalabilidad independiente:** Auth y Catálogo pueden escalar por separado
✅ **Mantenibilidad:** Cambios en usuarios solo afectan al servicio de Auth
✅ **Seguridad:** Un solo punto de gestión de usuarios y autenticación

## Próximos Pasos Recomendados

1. **Implementar el endpoint en el microservicio de Auth** (`/api/usuarios/{id}/existe`)
2. **Agregar manejo de timeouts** en las llamadas HTTP
3. **Implementar circuit breaker** (ej: Resilience4j) para manejar fallos del servicio de Auth
4. **Agregar autenticación entre microservicios** (ej: JWT, API Keys)
5. **Considerar usar Feign Client** en lugar de RestTemplate para código más limpio
6. **Implementar service discovery** (ej: Eureka) si se agregan más microservicios

## Notas Importantes

⚠️ **El microservicio de Auth debe estar corriendo** para que Catálogo funcione correctamente
⚠️ Si Auth no está disponible, las validaciones de usuario fallarán (retornarán `false`)
⚠️ Revisar que el puerto 9090 sea correcto para el microservicio de Auth
