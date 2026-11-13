package com.ecommerce.catalogo.infraestructura.driver_adapters.jpa_repository.producto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoDataJpaRepository extends JpaRepository<ProductoData, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ProductoData p set p.stock = p.stock - :cantidad "
            + "where p.id = :id and p.stock >= :cantidad")
    int descontarStock(@Param("id") Long id, @Param("cantidad") int cantidad);
}
