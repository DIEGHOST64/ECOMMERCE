package com.ecommerce.catalogo.application.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String role = request.getHeader("X-User-Role");
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Endpoints que solo ADMIN puede ejecutar
        if (path.startsWith("/api/ecommerce/producto") && 
            (method.equals("POST") || method.equals("DELETE"))) {
            if (!"ADMIN".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\": \"Acceso denegado. Solo administradores pueden realizar esta acción.\"}");
                response.setContentType("application/json");
                return false;
            }
        }

        // Endpoint para ver todas las compras - solo ADMIN
        if (path.equals("/api/ecommerce/compras") && method.equals("GET")) {
            if (!"ADMIN".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\": \"Acceso denegado. Solo administradores pueden ver todas las compras.\"}");
                response.setContentType("application/json");
                return false;
            }
        }

        return true;
    }
}
