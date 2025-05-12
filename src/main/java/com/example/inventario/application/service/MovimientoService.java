package com.example.inventario.application.service;

import com.example.inventario.domain.exceptions.InvalidStockException;
import com.example.inventario.domain.exceptions.MovimientoNotFoundException;
import com.example.inventario.domain.exceptions.ProductoNotFoundException;
import com.example.inventario.domain.model.Movimiento;
import com.example.inventario.domain.model.Producto;
import com.example.inventario.domain.ports.in.MovimientoUseCase;
import com.example.inventario.domain.ports.out.MovimientoRepositoryPort;
import com.example.inventario.domain.ports.out.ProductoRepositoryPort;
import com.example.inventario.infrastructure.adapters.out.MovimientoEntity;
import com.example.inventario.infrastructure.config.mapper.MovimientoMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona la lógica de negocio relacionada con movimientos de productos.
 */
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

    /**
     * Crea un nuevo movimiento y actualiza el stock del producto correspondiente.
     */
    @Override
    public Movimiento crearMovimiento(Movimiento movimiento) {
        // Obtener el producto relacionado con el movimiento
        Producto producto = productoRepository.buscarPorId(movimiento.getProducto().getId())
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));

        // Aplicar el cambio de stock según el tipo de movimiento
        if (movimiento.getTipo() == Movimiento.TipoMovimiento.ENTRADA) {
            productoService.entradaStock(producto.getId(), movimiento.getCantidad());
        } else if (movimiento.getTipo() == Movimiento.TipoMovimiento.SALIDA) {
            productoService.salidaStock(producto.getId(), movimiento.getCantidad());
        }

        // Guardar el movimiento en la base de datos
        return movimientoRepository.guardar(movimiento);
    }

    /**
     * Busca un movimiento por su ID.
     */
    @Override
    public Movimiento obtenerMovimientoPorId(Long id) {
        return movimientoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con ID: " + id));
    }

    /**
     * Registra un movimiento y actualiza el stock del producto.
     */
    @Override
    public Movimiento registrarMovimiento(Movimiento movimiento) {
        Producto producto = productoRepository.buscarPorId(movimiento.getProducto().getId())
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));

        if (movimiento.getTipo() == Movimiento.TipoMovimiento.ENTRADA) {
            productoService.entradaStock(producto.getId(), movimiento.getCantidad());
        } else if (movimiento.getTipo() == Movimiento.TipoMovimiento.SALIDA) {
            productoService.salidaStock(producto.getId(), movimiento.getCantidad());
        }

        return movimientoRepository.guardar(movimiento);
    }

    /**
     * Obtiene todos los movimientos relacionados con un producto.
     */
    @Override
    public List<Movimiento> obtenerMovimientosPorProducto(Long idProducto) {
        return movimientoRepository.buscarPorProductoId(idProducto);
    }

    /**
     * Devuelve la lista completa de movimientos registrados.
     */
    @Override
    public List<Movimiento> obtenerTodosLosMovimientos() {
        return movimientoRepository.obtenerTodos();
    }

    /**
     * Busca movimientos filtrando por ID de producto y/o tipo de movimiento.
     */
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

        // Convertir las entidades a modelos de dominio
        return resultados.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Elimina un movimiento y revierte su efecto en el stock del producto asociado.
     */
    @Override
    public void eliminarMovimiento(Long idMovimiento) {
        Movimiento movimiento = movimientoRepository.buscarPorId(idMovimiento)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        // Eliminar el movimiento
        movimientoRepository.eliminarPorId(idMovimiento);

        // Recuperar el producto afectado
        Producto producto = productoRepository.buscarPorId(movimiento.getProducto().getId())
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));

        // Revertir el efecto del movimiento sobre el stock
        if (movimiento.getTipo() == Movimiento.TipoMovimiento.ENTRADA) {
            productoService.salidaStock(producto.getId(), movimiento.getCantidad());
        } else if (movimiento.getTipo() == Movimiento.TipoMovimiento.SALIDA) {
            productoService.entradaStock(producto.getId(), movimiento.getCantidad());
        }
    }

    /**
     * Actualiza un movimiento existente y ajusta el stock del producto.
     */
    @Override
    public Movimiento actualizarMovimiento(Long id, Movimiento movimientoActualizado) {
        Movimiento movimientoExistente = movimientoRepository.buscarPorId(id)
                .orElseThrow(() -> new MovimientoNotFoundException("Movimiento no encontrado con id: " + id));

        Producto producto = productoRepository.buscarPorId(movimientoExistente.getProducto().getId())
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));

        // Revertir el efecto del movimiento anterior
        if (movimientoExistente.getTipo() == Movimiento.TipoMovimiento.ENTRADA) {
            producto.setStock(producto.getStock() - movimientoExistente.getCantidad());
        } else if (movimientoExistente.getTipo() == Movimiento.TipoMovimiento.SALIDA) {
            producto.setStock(producto.getStock() + movimientoExistente.getCantidad());
        }

        // Aplicar el nuevo movimiento
        if (movimientoActualizado.getTipo() == Movimiento.TipoMovimiento.ENTRADA) {
            producto.setStock(producto.getStock() + movimientoActualizado.getCantidad());
        } else if (movimientoActualizado.getTipo() == Movimiento.TipoMovimiento.SALIDA) {
            if (producto.getStock() < movimientoActualizado.getCantidad()) {
                throw new InvalidStockException("Stock insuficiente para actualizar la salida.");
            }
            producto.setStock(producto.getStock() - movimientoActualizado.getCantidad());
        }

        // Guardar los cambios en el producto y el movimiento
        productoRepository.guardar(producto);
        movimientoExistente.setCantidad(movimientoActualizado.getCantidad());
        movimientoExistente.setTipo(movimientoActualizado.getTipo());
        movimientoExistente.setDescripcion(movimientoActualizado.getDescripcion());

        return movimientoRepository.guardar(movimientoExistente);
    }

    /**
     * Calcula y actualiza el factor de rotación de un producto.
     */
    @Override
    public void recalcularFactorDeRotacion(Long idProducto) {
        Producto producto = productoRepository.buscarPorId(idProducto)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));

        // Obtener todos los movimientos del producto
        List<Movimiento> movimientos = movimientoRepository.buscarPorProductoId(idProducto);

        // Calcular la cantidad total vendida (solo salidas)
        int totalVendidas = 0;
        for (Movimiento movimiento : movimientos) {
            if (movimiento.getTipo() == Movimiento.TipoMovimiento.SALIDA) {
                totalVendidas += movimiento.getCantidad();
            }
        }

        // Calcular el factor de rotación simple
        double stockPromedio = producto.getStock();  // En entornos reales, se debería calcular con base en históricos
        double factorDeRotacion = stockPromedio != 0 ? totalVendidas / stockPromedio : 0;

        // Guardar el nuevo factor de rotación
        producto.setFactorDeRotacion(factorDeRotacion);
        productoRepository.guardar(producto);
    }
}
