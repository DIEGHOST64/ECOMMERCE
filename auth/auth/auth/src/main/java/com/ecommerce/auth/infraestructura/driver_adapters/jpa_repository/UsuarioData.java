package com.ecommerce.auth.infraestructura.driver_adapters.jpa_repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

@Entity
@Table(name = "usuario",
        uniqueConstraints = @UniqueConstraint(name = "uk_usuario_email", columnNames = "email"))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 30)
    private String telefono;

    @Column(nullable = false, length = 20)
    private String role;

    private Integer edad;

    @PrePersist
    @PreUpdate
    void normalizar() {
        if (role == null || role.isBlank()) {
            role = "USER";
        }
        if (email != null) {
            email = email.toLowerCase(Locale.ROOT);
        }
    }
}
