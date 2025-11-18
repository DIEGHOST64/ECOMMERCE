package com.ecommerce.catalogo.infraestructure.driver_adapters.jpa_repository.carrito;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface CarritoDataJpaRepository extends JpaRepository<CarritoData, Long> {

    Optional<CarritoData> findByUsuarioId(Long usuarioId);
    
    @org.springframework.transaction.annotation.Transactional
    void deleteByUsuarioId(Long usuarioId);
}
