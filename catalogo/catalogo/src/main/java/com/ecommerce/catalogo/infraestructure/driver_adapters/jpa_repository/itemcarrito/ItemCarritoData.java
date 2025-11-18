package com.ecommerce.catalogo.infraestructure.driver_adapters.jpa_repository.itemcarrito;

import com.ecommerce.catalogo.infraestructure.driver_adapters.jpa_repository.carrito.CarritoData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "item_carrito")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCarritoData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItemCarrito;

    private Long productoId;
    private String nombreProducto;
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "id_carrito")
    private CarritoData carrito;
}
