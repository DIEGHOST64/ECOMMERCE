package com.ecommerce.catalogo.infraestructure.driver_adapters.jpa_repository.compra;

import com.ecommerce.catalogo.infraestructure.driver_adapters.jpa_repository.itemcarrito.ItemCarritoData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compras")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompraData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long usuarioId;
    private LocalDateTime fechaCompra;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "compra_id")
    private List<ItemCarritoData> items = new ArrayList<>();
    
    private Double total;
    private String estado;
}
