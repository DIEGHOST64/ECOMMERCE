package com.ecommerce.catalogo.infraestructura.entry_points.mapper;

import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.Compra;
import com.ecommerce.catalogo.domain.model.ItemCarrito;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.infraestructura.entry_points.dto.CarritoResponse;
import com.ecommerce.catalogo.infraestructura.entry_points.dto.CompraResponse;
import com.ecommerce.catalogo.infraestructura.entry_points.dto.ItemCarritoResponse;
import com.ecommerce.catalogo.infraestructura.entry_points.dto.ProductoRequest;
import com.ecommerce.catalogo.infraestructura.entry_points.dto.ProductoResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class TiendaDtoMapper {

    public Producto toDomain(ProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setImagenUrl(request.imagenUrl());
        return producto;
    }

    public ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getImagenUrl());
    }

    public CarritoResponse toResponse(Carrito carrito) {
        return new CarritoResponse(
                carrito.getId(),
                carrito.getUsuarioId(),
                toItemResponses(carrito.getItems()),
                carrito.getPrecioTotal());
    }

    public CompraResponse toResponse(Compra compra) {
        return new CompraResponse(
                compra.getId(),
                compra.getUsuarioId(),
                compra.getFechaCompra(),
                toItemResponses(compra.getItems()),
                compra.getTotal(),
                compra.getEstado());
    }

    private static List<ItemCarritoResponse> toItemResponses(List<ItemCarrito> items) {
        if (items == null) {
            return Collections.emptyList();
        }
        return items.stream().map(TiendaDtoMapper::toItemResponse).toList();
    }

    private static ItemCarritoResponse toItemResponse(ItemCarrito item) {
        return new ItemCarritoResponse(
                item.getId(),
                item.getProductoId(),
                item.getNombreProducto(),
                item.getPrecioUnitario(),
                item.getCantidad(),
                item.getSubtotal(),
                item.getImagenUrl());
    }
}
