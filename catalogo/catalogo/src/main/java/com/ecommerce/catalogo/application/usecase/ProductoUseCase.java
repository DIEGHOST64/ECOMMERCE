package com.ecommerce.catalogo.application.usecase;

import com.ecommerce.catalogo.application.port.in.ProductoService;
import com.ecommerce.catalogo.domain.exception.DatosInvalidosException;
import com.ecommerce.catalogo.domain.exception.ProductoNoEncontradoException;
import com.ecommerce.catalogo.domain.exception.StockInsuficienteException;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.port.out.ProductoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class ProductoUseCase implements ProductoService {

    private final ProductoGateway productoGateway;

    @Override
    @Transactional
    public Producto guardarProducto(Producto producto) {
        validar(producto);
        producto.setId(null);
        return productoGateway.guardar(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto obtenerProducto(Long id) {
        if (id == null) {
            throw new DatosInvalidosException("El id del producto es obligatorio");
        }
        Producto producto = productoGateway.buscarPorId(id);
        if (producto == null) {
            throw new ProductoNoEncontradoException("Producto no encontrado con ID: " + id);
        }
        return producto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> obtenerTodos() {
        return productoGateway.obtenerTodos();
    }

    @Override
    @Transactional
    public Producto actualizarProducto(Producto producto) {
        if (producto == null || producto.getId() == null) {
            throw new DatosInvalidosException("El id del producto es obligatorio");
        }
        obtenerProducto(producto.getId());
        validar(producto);
        return productoGateway.guardar(producto);
    }

    @Override
    @Transactional
    public void eliminarProducto(Long id) {
        obtenerProducto(id);
        productoGateway.eliminar(id);
    }

    @Override
    @Transactional
    public void descontarStock(Long productoId, int cantidad) {
        if (productoId == null || cantidad <= 0) {
            throw new DatosInvalidosException("Producto y cantidad son obligatorios");
        }
        if (!productoGateway.descontarStock(productoId, cantidad)) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para el producto " + productoId);
        }
    }

    private void validar(Producto producto) {
        if (producto == null) {
            throw new DatosInvalidosException("Los datos del producto son obligatorios");
        }
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new DatosInvalidosException("El nombre del producto es obligatorio");
        }
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DatosInvalidosException("El precio debe ser mayor a cero");
        }
        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new DatosInvalidosException("El stock no puede ser negativo");
        }
    }
}
