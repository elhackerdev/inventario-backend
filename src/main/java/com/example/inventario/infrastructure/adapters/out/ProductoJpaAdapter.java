package com.example.inventario.infrastructure.adapters.out;

import com.example.inventario.domain.model.Producto;
import com.example.inventario.domain.ports.out.ProductoRepositoryPort;
import com.example.inventario.infrastructure.config.mapper.ProductoMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProductoJpaAdapter implements ProductoRepositoryPort {

    private final JpaProductoRepository jpaRepository;
    private final ProductoMapper mapper;

    public ProductoJpaAdapter(JpaProductoRepository jpaRepository, ProductoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Producto guardar(Producto producto) {
        Producto entity = mapper.toEntity(producto);
        Producto saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Producto> buscarPorCodigo(String codigo) {
        return Optional.empty();
    }

    @Override
    public List<Producto> buscarPorCriterios(String nombre, String categoria, String codigo) {
        // Este método depende de una consulta personalizada en JpaProductoRepository
        return jpaRepository
                .findByNombreContainingIgnoreCaseAndCategoriaAndCodigo(
                        nombre != null ? nombre : "",
                        categoria != null ? categoria : "",
                        codigo != null ? codigo : ""
                ).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Producto> obtenerTodos() {
        return List.of();
    }

    @Override
    public void eliminarPorId(Long id) {
        jpaRepository.deleteById(id);
    }
}
