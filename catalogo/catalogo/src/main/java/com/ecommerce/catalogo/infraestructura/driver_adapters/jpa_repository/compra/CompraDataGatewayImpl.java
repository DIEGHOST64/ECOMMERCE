package com.ecommerce.catalogo.infraestructura.driver_adapters.jpa_repository.compra;

import com.ecommerce.catalogo.domain.model.Compra;
import com.ecommerce.catalogo.domain.port.out.CompraGateway;
import com.ecommerce.catalogo.infraestructura.mapper.MapperCompra;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CompraDataGatewayImpl implements CompraGateway {

    private final CompraDataJpaRepository repository;
    private final MapperCompra mapper;

    @Override
    public Compra guardar(Compra compra) {
        CompraData data = mapper.toData(compra);
        return mapper.toDomain(repository.save(data));
    }

    @Override
    public Compra buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    public List<Compra> buscarPorUsuarioId(Long usuarioId) {
        return repository.findByUsuarioIdOrderByFechaCompraDesc(usuarioId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Compra> obtenerTodas() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }
}
