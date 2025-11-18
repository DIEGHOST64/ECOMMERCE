package com.ecommerce.catalogo.infraestructure.driver_adapters.jpa_repository.compra;

import com.ecommerce.catalogo.domain.model.Compra;
import com.ecommerce.catalogo.domain.model.gateway.CompraGateway;
import com.ecommerce.catalogo.infraestructure.mapper.MapperCompra;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

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
        return repository.findByUsuarioId(usuarioId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Compra> obtenerTodas() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}