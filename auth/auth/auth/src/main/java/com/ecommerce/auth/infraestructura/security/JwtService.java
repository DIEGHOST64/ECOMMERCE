package com.ecommerce.auth.infraestructura.security;

import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.port.out.TokenGateway;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService implements TokenGateway {

    private final SecretKey key;
    private final long expirationMillis;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes:1440}") long expirationMinutes) {
        byte[] bytes = secret == null ? new byte[0] : secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET debe tener al menos 32 caracteres (256 bits)");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.expirationMillis = Duration.ofMinutes(expirationMinutes).toMillis();
    }

    @Override
    public String generarToken(Usuario usuario) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(usuario.getId()))
                .claim("email", usuario.getEmail())
                .claim("nombre", usuario.getNombre())
                .claim("role", usuario.getRole())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(key)
                .compact();
    }

    @Override
    public long expiracionEnSegundos() {
        return expirationMillis / 1000;
    }

    public AuthenticatedUser parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new AuthenticatedUser(
                Long.valueOf(claims.getSubject()),
                claims.get("email", String.class),
                claims.get("nombre", String.class),
                claims.get("role", String.class));
    }
}
