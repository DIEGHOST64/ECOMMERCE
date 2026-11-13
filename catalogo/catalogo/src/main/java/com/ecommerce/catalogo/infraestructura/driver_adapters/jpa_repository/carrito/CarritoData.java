package com.ecommerce.catalogo.infraestructura.driver_adapters.jpa_repository.carrito;

import com.ecommerce.catalogo.infraestructura.driver_adapters.jpa_repository.itemcarrito.ItemCarritoData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrito",
        uniqueConstraints = @UniqueConstraint(name = "uk_carrito_usuario", columnNames = "usuario_id"))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarritoData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCarrito;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    private BigDecimal precioTotal;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarritoData> items = new ArrayList<>();
}
