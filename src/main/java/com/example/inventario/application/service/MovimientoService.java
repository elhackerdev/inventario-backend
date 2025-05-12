package com.example.inventario.application.service;

import com.example.inventario.domain.exceptions.InvalidStockException;
import com.example.inventario.domain.exceptions.MovimientoNotFoundException;
import com.example.inventario.domain.exceptions.ProductoNotFoundException;
import com.example.inventario.domain.model.Movimiento;
import com.example.inventario.domain.model.Producto;
import com.example.inventario.domain.ports.in.MovimientoUseCase;
import com.example.inventario.domain.ports.out.MovimientoRepositoryPort;
import com.example.inventario.domain.ports.out.ProductoRepositoryPort;
import com.example.inventario.infrastructure.adapters.in.dto.ResultadoOperacionDTO;
import com.example.inventario.infrastructure.adapters.out.MovimientoEntity;
import com.example.inventario.infrastructure.config.mapper.MovimientoMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimientoService implements MovimientoUseCase {

    private final MovimientoRepositoryPort movimientoRepository;
    private final ProductoRepositoryPort productoRepository;
    private final ProductoService productoService;
    private final MovimientoMapper mapper;

    public MovimientoService(MovimientoRepositoryPort movimientoRepository, ProductoRepositoryPort productoRepository, ProductoService productoService, MovimientoMapper mapper) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.productoService = productoService;
        this.mapper = mapper;
    }

    @Override
    public Movimiento crearMovimiento(Movimiento movimiento) {
        // Obtener el producto relacionado con el movimiento
        Producto producto = productoRepository.buscarPorId(movimiento.getProducto().getId())
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));

        if (movimiento.getTipo() == Movimiento.TipoMovimiento.ENTRADA) {
            productoService.entradaStock(producto.getId(),movimiento.getCantidad());
        } else if (movimiento.getTipo() == Movimiento.TipoMovimiento.SALIDA) {
            productoService.salidaStock(producto.getId(),movimiento.getCantidad());
        }
        Movimiento movimientoGuardado = movimientoRepository.guardar(movimiento);

        return movimientoGuardado;
    }

    @Override
    public Movimiento obtenerMovimientoPorId(Long id) {
        return movimientoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con ID: " + id));
    }

    @Override
    public Movimiento registrarMovimiento(Movimiento movimiento) {
        // Obtener el producto relacionado con el movimiento
        Producto producto = productoRepository.buscarPorId(movimiento.getProducto().getId())
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));

        // Dependiendo del tipo de movimiento, realizar la entrada o salida de stock
        if (movimiento.getTipo() == Movimiento.TipoMovimiento.ENTRADA) {
            productoService.entradaStock(producto.getId(),movimiento.getCantidad());
        } else if (movimiento.getTipo() == Movimiento.TipoMovimiento.SALIDA) {
           productoService.salidaStock(producto.getId(),movimiento.getCantidad());
        }
        // Guardar el movimiento en la base de datos
        Movimiento movimientoGuardado = movimientoRepository.guardar(movimiento);


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
    public List<Movimiento> buscarMovimientos(Long productoId, String tipo) {
        List<MovimientoEntity> resultados;

        if (productoId != null && tipo != null) {
            Movimiento.TipoMovimiento tipoMovimiento = Movimiento.TipoMovimiento.valueOf(tipo.toUpperCase());
            resultados = movimientoRepository.findByProductoIdAndTipo(productoId, tipoMovimiento);
        } else if (productoId != null) {
            resultados = movimientoRepository.findByProductoId(productoId);
        } else if (tipo != null) {
            Movimiento.TipoMovimiento tipoMovimiento = Movimiento.TipoMovimiento.valueOf(tipo.toUpperCase());
            resultados = movimientoRepository.findByTipo(tipoMovimiento);
        } else {
            resultados = movimientoRepository.findAll();
        }

        return resultados.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarMovimiento(Long idMovimiento) {
        Movimiento movimiento = movimientoRepository.buscarPorId(idMovimiento)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        // Eliminar el movimiento
        movimientoRepository.eliminarPorId(idMovimiento);

        // Ajustar el stock del producto relacionado
        Producto producto = productoRepository.buscarPorId(movimiento.getProducto().getId())
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));

       if (movimiento.getTipo() == Movimiento.TipoMovimiento.ENTRADA) {
            productoService.salidaStock(producto.getId(),movimiento.getCantidad());

        } else if (movimiento.getTipo() == Movimiento.TipoMovimiento.SALIDA) {
            productoService.entradaStock(producto.getId(),movimiento.getCantidad());
        }
    }

    @Override
    public Movimiento actualizarMovimiento(Long id, Movimiento movimientoActualizado) {
        // Buscar el movimiento existente
        Movimiento movimientoExistente = movimientoRepository.buscarPorId(id)
                .orElseThrow(() -> new MovimientoNotFoundException("Movimiento no encontrado con id: " + id));

        // Obtener el producto asociado
        Producto producto = productoRepository.buscarPorId(movimientoExistente.getProducto().getId())
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));

        // Revertir el movimiento anterior del stock
        if (movimientoExistente.getTipo() == Movimiento.TipoMovimiento.ENTRADA) {
            producto.setStock(producto.getStock() - movimientoExistente.getCantidad());
        } else if (movimientoExistente.getTipo() == Movimiento.TipoMovimiento.SALIDA) {
            producto.setStock(producto.getStock() + movimientoExistente.getCantidad());
        }

        // Aplicar el nuevo movimiento al stock
        if (movimientoActualizado.getTipo() == Movimiento.TipoMovimiento.ENTRADA) {
            producto.setStock(producto.getStock() + movimientoActualizado.getCantidad());
        } else if (movimientoActualizado.getTipo() == Movimiento.TipoMovimiento.SALIDA) {
            if (producto.getStock() < movimientoActualizado.getCantidad()) {
                throw new InvalidStockException("Stock insuficiente para actualizar la salida.");
            }
            producto.setStock(producto.getStock() - movimientoActualizado.getCantidad());
        }

        // Actualizar el producto en la base de datos
        productoRepository.guardar(producto);

        // Actualizar los datos del movimiento
        movimientoExistente.setCantidad(movimientoActualizado.getCantidad());
        movimientoExistente.setTipo(movimientoActualizado.getTipo());
        movimientoExistente.setDescripcion(movimientoActualizado.getDescripcion());


        return movimientoRepository.guardar(movimientoExistente);
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
