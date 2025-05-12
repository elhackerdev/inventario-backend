package com.example.inventario.infrastructure.adapters.out;


import com.example.inventario.domain.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface JpaMovimientoRepository extends JpaRepository<MovimientoEntity, Long> {

    // Obtener todos los movimientos de un producto
    List<MovimientoEntity> findByProductoId(Long productoId);

    List<MovimientoEntity> findByProductoIdAndTipo(Long productoId, Movimiento.TipoMovimiento tipo);


    List<MovimientoEntity> findByTipo(Movimiento.TipoMovimiento tipo);

    @Query("SELECT SUM(m.cantidad * m.producto.precio) FROM MovimientoEntity m WHERE m.producto.id = :productoId AND m.tipo = 'SALIDA'")
    Double sumarCostoVentasPorProducto(@Param("productoId") Long productoId);
}
