package com.example.inventario.application.service;

import com.example.inventario.domain.exceptions.InvalidStockException;
import com.example.inventario.domain.exceptions.ProductoNotFoundException;
import com.example.inventario.domain.model.Movimiento;
import com.example.inventario.domain.model.Producto;
import com.example.inventario.domain.ports.in.MovimientoUseCase;
import com.example.inventario.domain.ports.out.MovimientoRepositoryPort;
import com.example.inventario.domain.ports.out.ProductoRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimientoService implements MovimientoUseCase {

    private final MovimientoRepositoryPort movimientoRepository;
    private final ProductoRepositoryPort productoRepository;

    public MovimientoService(MovimientoRepositoryPort movimientoRepository, ProductoRepositoryPort productoRepository) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public Movimiento obtenerMovimientoPorId(Long id) {
        return movimientoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con ID: " + id));
    }

    @Override
    public Movimiento registrarMovimiento(Movimiento movimiento) {
        // Obtener el producto relacionado con el movimiento
        Producto producto = productoRepository.buscarPorId(movimiento.getIdProducto())
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));

        // Dependiendo del tipo de movimiento, realizar la entrada o salida de stock
        if (movimiento.getTipo() == Movimiento.TipoMovimiento.ENTRADA) {
            producto.setStock(producto.getStock() + movimiento.getCantidad());
        } else if (movimiento.getTipo() == Movimiento.TipoMovimiento.SALIDA) {
            if (producto.getStock() < movimiento.getCantidad()) {
                throw new InvalidStockException("Stock insuficiente para la salida.");
            }
            producto.setStock(producto.getStock() - movimiento.getCantidad());
        }

        // Guardar el movimiento en la base de datos
        Movimiento movimientoGuardado = movimientoRepository.guardar(movimiento);

        // Actualizar el stock del producto en la base de datos
        productoRepository.guardar(producto);

        return movimientoGuardado;
    }

    @Override
    public List<Movimiento> obtenerMovimientosPorProducto(Long idProducto) {
        return movimientoRepository.buscarPorProductoId(idProducto);
    }

    @Override
    public List<Movimiento> obtenerTodosLosMovimientos() {
        return movimientoRepository.obtenerTodos();
    }

    @Override
    public void eliminarMovimiento(Long idMovimiento) {
        Movimiento movimiento = movimientoRepository.buscarPorId(idMovimiento)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        // Eliminar el movimiento
        movimientoRepository.eliminarPorId(idMovimiento);

        // Ajustar el stock del producto relacionado
        Producto producto = productoRepository.buscarPorId(movimiento.getIdProducto())
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));

        if (movimiento.getTipo() == Movimiento.TipoMovimiento.ENTRADA) {
            producto.setStock(producto.getStock() - movimiento.getCantidad());
        } else if (movimiento.getTipo() == Movimiento.TipoMovimiento.SALIDA) {
            producto.setStock(producto.getStock() + movimiento.getCantidad());
        }

        // Guardar los cambios en el producto
        productoRepository.guardar(producto);
    }

    @Override
    public void recalcularFactorDeRotacion(Long idProducto) {
        Producto producto = productoRepository.buscarPorId(idProducto)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));

        // Obtener los movimientos históricos para el producto
        List<Movimiento> movimientos = movimientoRepository.buscarPorProductoId(idProducto);

        // Calcular el total de unidades vendidas (salidas)
        int totalVendidas = 0;
        for (Movimiento movimiento : movimientos) {
            if (movimiento.getTipo() == Movimiento.TipoMovimiento.SALIDA) {
                totalVendidas += movimiento.getCantidad();
            }
        }

        // Asumir que el factor de rotación puede ser el total de salidas dividido entre el stock promedio
        // (Para simplificar, en este ejemplo no estamos considerando un período de tiempo específico)
        double stockPromedio = producto.getStock();  // Este valor se puede calcular de manera más precisa si se lleva un historial de stock mensual o por períodos
        double factorDeRotacion = stockPromedio != 0 ? totalVendidas / stockPromedio : 0;

        // Actualizar el factor de rotación en el producto
        producto.setFactorDeRotacion(factorDeRotacion);

        // Guardar el producto con el factor de rotación actualizado
        productoRepository.guardar(producto);
    }
}
