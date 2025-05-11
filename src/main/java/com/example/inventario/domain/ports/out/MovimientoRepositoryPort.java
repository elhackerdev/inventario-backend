package com.example.inventario.domain.ports.out;

import com.example.inventario.domain.model.Movimiento;
import com.example.inventario.infrastructure.adapters.out.MovimientoEntity;

import java.util.List;
import java.util.Optional;

public interface MovimientoRepositoryPort {

    // Guardar un movimiento (entrada o salida)
    Movimiento guardar(Movimiento movimiento);

    // Obtener un movimiento por su ID
    Optional<Movimiento> buscarPorId(Long id);

    // Obtener todos los movimientos
    List<Movimiento> obtenerTodos();

    // Obtener todos los movimientos de un producto específico
    List<Movimiento> buscarPorProductoId(Long idProducto);

    List<Movimiento> buscarPorProducto(Long productoId);

    List<Movimiento> buscarMovimientos(Long productoId, String tipo);

    // Eliminar un movimiento
    void eliminarPorId(Long id);

    List<Movimiento> buscarTodos();

    List<MovimientoEntity> findByProductoIdAndTipo(Long productoId, Movimiento.TipoMovimiento tipoMovimiento);

    List<MovimientoEntity> findByProductoId(Long productoId);

    List<MovimientoEntity> findByTipo(Movimiento.TipoMovimiento tipoMovimiento);

    List<MovimientoEntity> findAll();
}
