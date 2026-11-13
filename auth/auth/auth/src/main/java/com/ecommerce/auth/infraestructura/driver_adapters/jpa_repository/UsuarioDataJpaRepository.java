package com.ecommerce.auth.infraestructura.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioDataJpaRepository extends JpaRepository<UsuarioData, Long> {

    Optional<UsuarioData> findByEmail(String email);

    Optional<UsuarioData> findByEmailIgnoreCase(String email);
}
