package com.example.inventario.infrastructure.adapters.out;

import com.example.inventario.domain.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JpaMovimientoRepository extends JpaRepository<Movimiento, Long> {

    // Obtener todos los movimientos de un producto
    List<Movimiento> findByProductoId(Long productoId);

    // Obtener movimientos por tipo (ENTRADA o SALIDA)
    List<Movimiento> findByTipo(String tipo);

    // Obtener movimientos de un producto por fecha
    List<Movimiento> findByProductoIdAndFechaBetween(Long productoId, LocalDate fechaInicio, LocalDate fechaFin);

    // Obtener todos los movimientos dentro de un rango de fechas
    List<Movimiento> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // Consultar movimientos recientes de un producto
    List<Movimiento> findTop5ByProductoIdOrderByFechaDesc(Long productoId);

    // Buscar movimientos por descripción (útil para búsquedas por detalle de la acción)
    List<Movimiento> findByDescripcionContainingIgnoreCase(String descripcion);
}
