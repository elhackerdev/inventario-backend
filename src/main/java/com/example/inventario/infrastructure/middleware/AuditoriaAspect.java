package com.example.inventario.infrastructure.middleware;

import com.example.inventario.domain.model.Movimiento;
import com.example.inventario.domain.model.Producto;
import com.example.inventario.domain.model.StockLog;
import com.example.inventario.domain.ports.out.RegistrarAuditoriaStockPort;
import com.example.inventario.infrastructure.adapters.out.JpaProductoRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditoriaAspect {

    private final RegistrarAuditoriaStockPort auditoriaStockPort;
    private final JpaProductoRepository productoRepository;

    public AuditoriaAspect(RegistrarAuditoriaStockPort port, JpaProductoRepository productoRepository) {
        this.auditoriaStockPort = port;
        this.productoRepository = productoRepository;
    }

    @Before("execution(* com.example.inventario.application.service.MovimientoService.crearMovimiento(..))")
    public void interceptarMovimiento(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Movimiento mov) {

            Long productoId = mov.getProducto().getId();
            int cantidad = mov.getCantidad();

            Producto producto = productoRepository.findById(productoId).orElse(null);
            if (producto == null) return;

            int stockAnterior = producto.getStock();
            int nuevoStock = mov.getTipo() == Movimiento.TipoMovimiento.ENTRADA
                    ? stockAnterior + cantidad
                    : stockAnterior - cantidad;

            StockLog log = new StockLog();
            log.setProductoId(productoId);
            log.setCantidadAnterior(stockAnterior);
            log.setCantidadNueva(nuevoStock);
            log.setOperacion(mov.getTipo().name());
            log.setFecha(LocalDateTime.now());

            auditoriaStockPort.registrarLog(log);
        }
    }
}
