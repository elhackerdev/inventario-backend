package com.example.inventario.domain.ports.in;

import com.example.inventario.domain.model.Movimiento;

import java.util.List;

public interface MovimientoUseCase {

    // Crear un movimiento (entrada o salida)
    Movimiento crearMovimiento(Movimiento movimiento);

    // Obtener un movimiento por su ID
    Movimiento obtenerMovimientoPorId(Long id);

    // Obtener el historial de movimientos de un producto específico
    List<Movimiento> obtenerMovimientosPorProducto(Long idProducto);

    // Obtener todos los movimientos (útil para visualizar el historial completo)
    List<Movimiento> obtenerTodosLosMovimientos();
    List<Movimiento> buscarMovimientos(Long productoId, String tipo);
    // Eliminar un movimiento (por si se requiere anulación)
    void eliminarMovimiento(Long id);
    Movimiento actualizarMovimiento(Long id, Movimiento movimiento);
    // Recalcular factor de rotación de un producto (puede ser llamado desde aquí o delegarse)
    void recalcularFactorDeRotacion(Long idProducto);
}
