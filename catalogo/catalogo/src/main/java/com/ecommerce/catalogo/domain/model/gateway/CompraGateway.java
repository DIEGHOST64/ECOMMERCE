package com.ecommerce.catalogo.domain.model.gateway;

import com.ecommerce.catalogo.domain.model.Compra;
import java.util.List;

public interface CompraGateway {
    Compra guardar(Compra compra);
    Compra buscarPorId(Long id);
    List<Compra> buscarPorUsuarioId(Long usuarioId);
    List<Compra> obtenerTodas();
}