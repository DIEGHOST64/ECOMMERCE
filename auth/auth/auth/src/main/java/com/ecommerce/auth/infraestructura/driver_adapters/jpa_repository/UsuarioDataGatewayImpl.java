package com.ecommerce.auth.infraestructura.driver_adapters.jpa_repository;

import com.ecommerce.auth.domain.exception.UsuarioNoEncontradoException;
import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.port.out.UsuarioGateway;
import com.ecommerce.auth.infraestructura.mapper.MapperUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UsuarioDataGatewayImpl implements UsuarioGateway {

    private final MapperUsuario mapperUsuario;
    private final UsuarioDataJpaRepository repository;

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioData data = mapperUsuario.toUsuarioData(usuario);
        return mapperUsuario.toUsuario(repository.save(data));
    }

    @Override
    public void eliminarPorID(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Usuario buscarPorID(Long id) {
        return repository.findById(id)
                .map(mapperUsuario::toUsuario)
                .orElse(null);
    }

    @Override
    public Usuario actualizarUsario(Usuario usuario) {
        UsuarioData existente = repository.findById(usuario.getId())
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuario.getId()));

        existente.setNombre(usuario.getNombre());
        existente.setEmail(usuario.getEmail());
        existente.setPassword(usuario.getPassword());
        existente.setTelefono(usuario.getTelefono());
        existente.setRole(usuario.getRole());
        existente.setEdad(usuario.getEdad());

        return mapperUsuario.toUsuario(repository.save(existente));
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        return repository.findByEmailIgnoreCase(email)
                .map(mapperUsuario::toUsuario)
                .orElse(null);
    }
}
