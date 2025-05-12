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
        MovimientoEntity entity = mapper.toEntity(movimiento); // convertir del dominio a entidad
        MovimientoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved); // volver a modelo de dominio
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
        return jpaRepository.findByProductoId(idProducto)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movimiento> buscarPorProducto(Long productoId) {
        return List.of();
    }

    @Override
    public List<Movimiento> buscarMovimientos(Long productoId, String tipo) {
        List<MovimientoEntity> resultados;

        if (productoId != null && tipo != null) {
            Movimiento.TipoMovimiento tipoMovimiento = Movimiento.TipoMovimiento.valueOf(tipo.toUpperCase());
            resultados = jpaRepository.findByProductoIdAndTipo(productoId, tipoMovimiento);
        } else if (productoId != null) {
            resultados = jpaRepository.findByProductoId(productoId);
        } else if (tipo != null) {
            Movimiento.TipoMovimiento tipoMovimiento = Movimiento.TipoMovimiento.valueOf(tipo.toUpperCase());
            resultados = jpaRepository.findByTipo(tipoMovimiento);
        } else {
            resultados = jpaRepository.findAll();
        }

        return resultados.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarPorId(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Movimiento> buscarTodos() {
        return jpaRepository.findAll()  // Obtiene la lista de MovimientoEntity
                .stream()  // Crea un Stream para procesar los elementos
                .map(mapper::toDomain)  // Convierte cada MovimientoEntity a Movimiento (modelo de dominio)
                .collect(Collectors.toList());  // Recoge el resultado en una lista
    }

    @Override
    public List<MovimientoEntity> findByProductoIdAndTipo(Long productoId, Movimiento.TipoMovimiento tipoMovimiento) {
        return jpaRepository.findByProductoIdAndTipo(productoId, tipoMovimiento);
    }

    @Override
    public List<MovimientoEntity> findByProductoId(Long productoId) {
        return jpaRepository.findByProductoId(productoId);
    }

    @Override
    public List<MovimientoEntity> findByTipo(Movimiento.TipoMovimiento tipoMovimiento) {
        return jpaRepository.findByTipo(tipoMovimiento);
    }

    @Override
    public List<MovimientoEntity> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Double sumarCostoVentasPorProducto(Long productoId) {
        return 0.0;
    }
}
