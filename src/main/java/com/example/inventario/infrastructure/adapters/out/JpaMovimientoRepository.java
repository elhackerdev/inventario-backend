package com.example.inventario.infrastructure.adapters.out;


import com.example.inventario.domain.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface JpaMovimientoRepository extends JpaRepository<MovimientoEntity, Long> {

    // Obtener todos los movimientos de un producto
    List<MovimientoEntity> findByProductoId(Long productoId);

    List<MovimientoEntity> findByProductoIdAndTipo(Long productoId, Movimiento.TipoMovimiento tipo);


    List<MovimientoEntity> findByTipo(Movimiento.TipoMovimiento tipo);
}
