package com.ecommerce.catalogo.infraestructure.mapper;

import com.ecommerce.catalogo.domain.model.Compra;
import com.ecommerce.catalogo.domain.model.ItemCarrito;
import com.ecommerce.catalogo.infraestructure.driver_adapters.jpa_repository.compra.CompraData;
import com.ecommerce.catalogo.infraestructure.driver_adapters.jpa_repository.itemcarrito.ItemCarritoData;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MapperCompra {

    public Compra toDomain(CompraData data) {
        if (data == null) return null;
        
        return new Compra(
            data.getId(),
            data.getUsuarioId(),
            data.getFechaCompra(),
            data.getItems().stream()
                .map(this::mapItemCarritoFromData)
                .collect(Collectors.toList()),
            data.getTotal(),
            data.getEstado()
        );
    }

    public CompraData toData(Compra compra) {
        if (compra == null) return null;
        
        CompraData data = new CompraData();
        data.setId(compra.getId());
        data.setUsuarioId(compra.getUsuarioId());
        data.setFechaCompra(compra.getFechaCompra());
        data.setTotal(compra.getTotal());
        data.setEstado(compra.getEstado());
        
        if (compra.getItems() != null) {
            data.setItems(compra.getItems().stream()
                .map(this::mapItemCarritoToData)
                .collect(Collectors.toList()));
        }
        
        return data;
    }

    private ItemCarrito mapItemCarritoFromData(ItemCarritoData data) {
        return new ItemCarrito(
            data.getIdItemCarrito(),
            null, // carritoId no es necesario para items en una compra
            data.getProductoId(),
            data.getNombreProducto(),
            data.getPrecioUnitario(),
            data.getCantidad(),
            data.getSubtotal()
        );
    }

    private ItemCarritoData mapItemCarritoToData(ItemCarrito item) {
        ItemCarritoData data = new ItemCarritoData();
        // No establecemos el ID para que JPA genere uno nuevo
        data.setProductoId(item.getProductoId());
        data.setNombreProducto(item.getNombreProducto());
        data.setPrecioUnitario(item.getPrecioUnitario());
        data.setCantidad(item.getCantidad());
        data.setSubtotal(item.getSubtotal());
        return data;
    }
}
