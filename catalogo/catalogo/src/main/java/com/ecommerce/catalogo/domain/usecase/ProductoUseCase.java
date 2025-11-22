package com.ecommerce.catalogo.domain.usecase;

import com.ecommerce.catalogo.domain.exception.ProductoNoEncontradoException;
import com.ecommerce.catalogo.domain.exception.StockInsuficienteException;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.model.gateway.ProductoGateway;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ProductoUseCase {

    private final ProductoGateway productoGateway;


    public Producto guardarProducto(Producto producto) {
        return productoGateway.guardar(producto);
    }


    public Producto obtenerProducto(Long id) {
        Producto producto = productoGateway.buscarPorId(id);
        if (producto == null) {
            throw new ProductoNoEncontradoException("Producto no encontrado con ID: " + id);
        }
        return producto;
    }

    public Producto actualizarStock(Long id, int cantidadARestar) {
        Producto producto = obtenerProducto(id);
        if (producto.getStock() < cantidadARestar) {
            throw new StockInsuficienteException("Stock insuficiente. Stock disponible: " + producto.getStock());
        }
        producto.setStock(producto.getStock() - cantidadARestar);
        return productoGateway.guardar(producto);
    }

    public Producto restaurarStock(Long id, int cantidadARestaurar) {
        Producto producto = obtenerProducto(id);
        producto.setStock(producto.getStock() + cantidadARestaurar);
        return productoGateway.guardar(producto);
    }

    public List<Producto> obtenerTodos() {
        return productoGateway.obtenerTodos();
    }

    public Producto actualizarProducto(Producto producto) {
        return productoGateway.guardar(producto);
    }

    public void eliminarProducto(Long id) {
        productoGateway.eliminar(id);
    }
}
