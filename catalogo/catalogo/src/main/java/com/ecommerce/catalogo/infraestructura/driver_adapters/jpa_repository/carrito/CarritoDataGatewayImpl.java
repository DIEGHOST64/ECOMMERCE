package com.ecommerce.catalogo.infraestructura.driver_adapters.jpa_repository.carrito;

import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.port.out.CarritoGateway;
import com.ecommerce.catalogo.infraestructura.mapper.MapperCarrito;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class CarritoDataGatewayImpl implements CarritoGateway {

    private final CarritoDataJpaRepository repository;
    private final MapperCarrito mapper;

    @Override
    @Transactional
    public Carrito guardar(Carrito carrito) {
        if (carrito.getUsuarioId() == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo");
        }
        CarritoData entity = mapper.toData(carrito);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Carrito buscarPorUsuarioId(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId)
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    @Transactional
    public void eliminarCarrito(Long carritoId) {
        repository.deleteById(carritoId);
    }

    @Override
    @Transactional
    public void eliminarPorUsuarioId(Long usuarioId) {
        repository.findByUsuarioId(usuarioId)
                .ifPresent(repository::delete);
    }
}
