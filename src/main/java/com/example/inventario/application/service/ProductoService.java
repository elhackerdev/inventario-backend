package com.example.inventario.application.service;

import com.example.inventario.domain.exceptions.ProductoNotFoundException;
import com.example.inventario.domain.model.Producto;
import com.example.inventario.domain.ports.in.ProductoUseCase;
import com.example.inventario.domain.ports.out.MovimientoRepositoryPort;
import com.example.inventario.domain.ports.out.ProductoRepositoryPort;
import com.example.inventario.infrastructure.adapters.in.dto.MovimientoDTO;
import com.example.inventario.infrastructure.adapters.in.dto.ResultadoOperacionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de aplicación para gestionar operaciones relacionadas con productos.
 */
@Service
public class ProductoService implements ProductoUseCase {

    private final ProductoRepositoryPort repository;
    private final MovimientoRepositoryPort movimientoRepositoryPort;

    @Autowired
    private StockService stockService;

    public ProductoService(ProductoRepositoryPort repository, MovimientoRepositoryPort movimientoRepositoryPort) {
        this.repository = repository;
        this.movimientoRepositoryPort = movimientoRepositoryPort;
    }

    /**
     * Crea un nuevo producto si el código no está duplicado.
     */
    @Override
    public Producto crearProducto(Producto producto) {
        // Validación para evitar códigos repetidos
        if (repository.buscarPorCodigo(producto.getCodigo()).isPresent()) {
            throw new RuntimeException("El código del producto ya está registrado.");
        }

        // Almacenar el inventario inicial
        producto.setInventarioInicial(producto.getStock());

        return repository.guardar(producto);
    }

    /**
     * Obtiene un producto por su ID.
     */
    @Override
    public Producto obtenerProductoPorId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));
    }

    /**
     * Busca productos por nombre, categoría y/o código.
     */
    @Override
    public List<Producto> buscarProductos(String nombre, String categoria, String codigo) {
        return repository.buscarPorCriterios(nombre, categoria, codigo);
    }

    /**
     * Actualiza los datos de un producto, manteniendo su ID y fecha de creación.
     */
    @Override
    public Producto actualizarProducto(Long id, Producto producto) {
        Producto productoExistente = repository.buscarPorId(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));

        // Mantener la fecha original de creación y el ID
        producto.setId(id);
        producto.setFechaCreacion(productoExistente.getFechaCreacion());

        return repository.guardar(producto);
    }

    /**
     * Elimina un producto si existe.
     */
    @Override
    public void eliminarProducto(Long id) {
        Producto producto = repository.buscarPorId(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));

        repository.eliminarPorId(id);
    }

    /**
     * Registra una entrada de stock en un producto.
     */
    @Override
    public Producto entradaStock(Long idProducto, int cantidad) {
        Producto producto = obtenerProductoPorId(idProducto);

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de entrada debe ser positiva.");
        }

        producto.setStock(producto.getStock() + cantidad);
        repository.guardar(producto);

        return recalcularFactorDeRotacion(idProducto);
    }

    /**
     * Registra una salida de stock en un producto.
     */
    @Override
    public Producto salidaStock(Long idProducto, int cantidad) {
        Producto producto = obtenerProductoPorId(idProducto);

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de salida debe ser positiva.");
        }

        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException("No hay suficiente stock para esta salida.");
        }

        producto.setStock(producto.getStock() - cantidad);
        repository.guardar(producto);

        return recalcularFactorDeRotacion(idProducto);
    }

    /**
     * Verifica si el producto está por debajo del stock mínimo definido.
     */
    @Override
    public ResultadoOperacionDTO verificarStockMinimo(Long idProducto, MovimientoDTO movimiento) {
        ResultadoOperacionDTO resultado = new ResultadoOperacionDTO();
        Producto producto = obtenerProductoPorId(idProducto);

        int umbralStockBajo = 10; // Umbral definido para notificar stock bajo

        resultado.setMovimiento(movimiento);
        resultado.setStockBajo(producto.getStock() < umbralStockBajo);
        resultado.setCantidad(producto.getStock());
        resultado.setNombreProducto(producto.getNombre());

        // Notificación o acción adicional si se detecta bajo stock
        stockService.verificarStockBajo(producto, umbralStockBajo);

        return resultado;
    }

    /**
     * Recalcula el factor de rotación del inventario para un producto.
     */
    @Override
    public Producto recalcularFactorDeRotacion(Long idProducto) {
        Producto producto = obtenerProductoPorId(idProducto);

        // Costo de ventas acumulado (salidas * precio/costo unitario)
        double costoVentas = movimientoRepositoryPort.sumarCostoVentasPorProducto(idProducto);

        // Valores requeridos para el cálculo del inventario promedio
        double inventarioInicial = producto.getInventarioInicial();
        double inventarioFinal = producto.getStock();
        double inventarioPromedio = (inventarioInicial + inventarioFinal) / 2.0;

        // Cálculo del factor de rotación
        double rotacion = inventarioPromedio > 0 ? costoVentas / inventarioPromedio : 0.0;

        producto.setFactorDeRotacion(rotacion);
        return repository.guardar(producto);
    }
}
