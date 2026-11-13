package com.ecommerce.catalogo.infraestructura.mapper;

import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.ItemCarrito;
import com.ecommerce.catalogo.infraestructura.driver_adapters.jpa_repository.carrito.CarritoData;
import com.ecommerce.catalogo.infraestructura.driver_adapters.jpa_repository.itemcarrito.ItemCarritoData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MapperCarrito {

    public CarritoData toData(Carrito carrito) {
        CarritoData carritoData = new CarritoData();
        carritoData.setIdCarrito(carrito.getId());
        carritoData.setUsuarioId(carrito.getUsuarioId());
        carritoData.setPrecioTotal(carrito.getPrecioTotal());

        List<ItemCarritoData> items = new ArrayList<>();
        if (carrito.getItems() != null) {
            for (ItemCarrito item : carrito.getItems()) {
                ItemCarritoData itemData = new ItemCarritoData();
                itemData.setIdItemCarrito(item.getId());
                itemData.setProductoId(item.getProductoId());
                itemData.setNombreProducto(item.getNombreProducto());
                itemData.setPrecioUnitario(item.getPrecioUnitario());
                itemData.setCantidad(item.getCantidad());
                itemData.setSubtotal(item.getSubtotal());
                itemData.setImagenUrl(item.getImagenUrl());
                itemData.setCarrito(carritoData);
                items.add(itemData);
            }
        }

        carritoData.setItems(items);
        return carritoData;
    }

    public Carrito toDomain(CarritoData carritoData) {
        if (carritoData == null) {
            return null;
        }

        Carrito carrito = new Carrito();
        carrito.setId(carritoData.getIdCarrito());
        carrito.setUsuarioId(carritoData.getUsuarioId());
        carrito.setPrecioTotal(carritoData.getPrecioTotal());

        List<ItemCarrito> items = new ArrayList<>();
        if (carritoData.getItems() != null) {
            for (ItemCarritoData itemData : carritoData.getItems()) {
                ItemCarrito item = new ItemCarrito();
                item.setId(itemData.getIdItemCarrito());
                item.setProductoId(itemData.getProductoId());
                item.setNombreProducto(itemData.getNombreProducto());
                item.setPrecioUnitario(itemData.getPrecioUnitario());
                item.setCantidad(itemData.getCantidad());
                item.setSubtotal(itemData.getSubtotal());
                item.setImagenUrl(itemData.getImagenUrl());
                item.setCarritoId(carritoData.getIdCarrito());
                items.add(item);
            }
        }

        carrito.setItems(items);
        return carrito;
    }
}
