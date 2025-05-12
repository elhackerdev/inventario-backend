package com.example.inventario.application.service;

import com.example.inventario.domain.exceptions.ProductoNotFoundException;
import com.example.inventario.domain.model.Producto;
import com.example.inventario.domain.ports.in.ProductoUseCase;
import com.example.inventario.domain.ports.out.MovimientoRepositoryPort;
import com.example.inventario.domain.ports.out.ProductoRepositoryPort;
import com.example.inventario.infrastructure.adapters.in.dto.MovimientoDTO;
import com.example.inventario.infrastructure.adapters.in.dto.ResultadoOperacionDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService implements ProductoUseCase {
    private final ProductoRepositoryPort repository;
    private final MovimientoRepositoryPort movimientoRepositoryPort;


    public ProductoService(ProductoRepositoryPort repository, MovimientoRepositoryPort movimientoRepositoryPort) {
        this.repository = repository;
        this.movimientoRepositoryPort = movimientoRepositoryPort;
    }

    @Override
    public Producto crearProducto(Producto producto) {
        // Validaciones de negocio, como verificar si el código ya existe
        if (repository.buscarPorCodigo(producto.getCodigo()).isPresent()) {
            throw new RuntimeException("El código del producto ya está registrado.");
        }
        producto.setInventarioInicial(producto.getStock());

        // Si no hay problemas, se guarda el producto
        return repository.guardar(producto);
    }

    @Override
    public Producto obtenerProductoPorId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));
    }

    @Override
    public List<Producto> buscarProductos(String nombre, String categoria, String codigo) {
        return repository.buscarPorCriterios(nombre, categoria, codigo);
    }

    @Override
    public Producto actualizarProducto(Long id, Producto producto) {
        // Verificar si el producto existe antes de intentar actualizarlo
        Producto productoExistente = repository.buscarPorId(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));

        // Aquí puedes actualizar los campos específicos, manteniendo el id
        producto.setId(id);
        producto.setFechaCreacion(productoExistente.getFechaCreacion());
        return repository.guardar(producto);
    }

    @Override
    public void eliminarProducto(Long id) {
        // Verificar si el producto existe antes de intentar eliminarlo
        Producto producto = repository.buscarPorId(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado"));

        repository.eliminarPorId(id);
    }

    @Override
    public Producto entradaStock(Long idProducto, int cantidad) {
        Producto producto = obtenerProductoPorId(idProducto);

        // Asegurarse de que la cantidad a ingresar sea positiva
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de entrada debe ser positiva.");
        }

        // Se ajusta el stock
        producto.setStock(producto.getStock() + cantidad);
        repository.guardar(producto);
        return recalcularFactorDeRotacion(idProducto);
    }

    @Override
    public Producto salidaStock(Long idProducto, int cantidad) {
        Producto producto = obtenerProductoPorId(idProducto);

        // Asegurarse de que el stock no sea menor que la cantidad a retirar
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de salida debe ser positiva.");
        }

        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException("No hay suficiente stock para esta salida.");
        }

        // Se ajusta el stock
        producto.setStock(producto.getStock() - cantidad);

        repository.guardar(producto);
        return recalcularFactorDeRotacion(idProducto);
    }

    @Override
    public ResultadoOperacionDTO verificarStockMinimo(Long idProducto, MovimientoDTO movimiento) {

        ResultadoOperacionDTO resultado = new ResultadoOperacionDTO();
        Producto producto = obtenerProductoPorId(idProducto);

        // Definir un umbral de stock bajo, por ejemplo, 10 unidades
        int umbralStockBajo = 10;
        resultado.setMovimiento(movimiento);
        resultado.setStockBajo(producto.getStock() < umbralStockBajo);
        resultado.setCantidad(producto.getStock());
        resultado.setNombreProducto(producto.getNombre());


        return  resultado;
    }

    @Override
    public Producto recalcularFactorDeRotacion(Long idProducto) {
        Producto producto = obtenerProductoPorId(idProducto);
        double costoVentas = movimientoRepositoryPort.sumarCostoVentasPorProducto(idProducto); // debes definir este método
        double inventarioInicial = producto.getInventarioInicial(); // debes guardar este dato cuando inicia el período
        double inventarioFinal = producto.getStock(); // stock actual
        double inventarioPromedio = (inventarioInicial + inventarioFinal) / 2.0;
        double rotacion = inventarioPromedio > 0 ? costoVentas / inventarioPromedio : 0.0;
        producto.setFactorDeRotacion(rotacion);
         return repository.guardar(producto);
    }
}
