package com.ecommerce.catalogo.infraestructura.driver_adapters.jpa_repository.compra;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraDataJpaRepository extends JpaRepository<CompraData, Long> {

    List<CompraData> findByUsuarioIdOrderByFechaCompraDesc(Long usuarioId);
}
