package com.ecommerce.catalogo.infraestructure.driver_adapters.jpa_repository.compra;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraDataJpaRepository extends JpaRepository<CompraData, Long> {
    List<CompraData> findByUsuarioId(Long usuarioId);
}