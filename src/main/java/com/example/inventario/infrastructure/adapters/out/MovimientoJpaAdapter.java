package com.example.inventario.infrastructure.adapters.out;

import com.example.inventario.domain.model.Movimiento;
import com.example.inventario.domain.ports.out.MovimientoRepositoryPort;
import com.example.inventario.infrastructure.config.mapper.MovimientoMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MovimientoJpaAdapter implements MovimientoRepositoryPort {

    private final JpaMovimientoRepository jpaRepository;
    private final MovimientoMapper mapper;

    public MovimientoJpaAdapter(JpaMovimientoRepository jpaRepository, MovimientoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Movimiento guardar(Movimiento movimiento) {
        Movimiento entity = mapper.toEntity(movimiento);
        Movimiento guardado = jpaRepository.save(entity);
        return mapper.toDomain(guardado);
    }

    @Override
    public Optional<Movimiento> buscarPorId(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Movimiento> obtenerTodos() {
        return List.of();
    }

    @Override
    public List<Movimiento> buscarPorProductoId(Long idProducto) {
        return List.of();
    }

    @Override
    public List<Movimiento> buscarPorProducto(Long productoId) {
        List<Movimiento> entidades = jpaRepository.findByProductoId(productoId);
        return entidades.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarPorId(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Movimiento> buscarTodos() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
