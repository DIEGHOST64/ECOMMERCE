package com.ecommerce.catalogo.application.port.in;

import com.ecommerce.catalogo.domain.model.Compra;

import java.util.List;

public interface CompraService {

    Compra realizarCompra(Long usuarioId);

    List<Compra> obtenerComprasPorUsuario(Long usuarioId);

    Compra obtenerCompraPorId(Long compraId);

    List<Compra> obtenerTodasLasCompras();
}
