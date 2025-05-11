package com.example.inventario.application.service;

import com.example.inventario.domain.exceptions.ProductoNotFoundException;
import com.example.inventario.domain.model.Producto;
import com.example.inventario.domain.ports.in.ProductoUseCase;
import com.example.inventario.domain.ports.out.ProductoRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService implements ProductoUseCase {
    private final ProductoRepositoryPort repository;

    public ProductoService(ProductoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Producto crearProducto(Producto producto) {
        // Validaciones de negocio, como verificar si el código ya existe
        if (repository.buscarPorCodigo(producto.getCodigo()).isPresent()) {
            throw new RuntimeException("El código del producto ya está registrado.");
        }

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
        return repository.guardar(producto);
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
        return repository.guardar(producto);
    }

    @Override
    public void verificarStockMinimo(Long idProducto) {
        Producto producto = obtenerProductoPorId(idProducto);

        // Definir un umbral de stock bajo, por ejemplo, 10 unidades
        int umbralStockBajo = 10;

        if (producto.getStock() < umbralStockBajo) {
            // Aquí podrías integrar el patrón Observer, enviando una alerta
            // Notificar sobre stock bajo (esto depende de la implementación específica de Observer)
            System.out.println("Alerta: El stock del producto " + producto.getNombre() + " es bajo.");
        }
    }

    @Override
    public void recalcularFactorDeRotacion(Long idProducto) {
        Producto producto = obtenerProductoPorId(idProducto);

        // Lógica para recalcular el factor de rotación (puede depender de la cantidad de ventas, movimientos, etc.)
        // Por ejemplo, podrías calcularlo basado en los movimientos históricos.
        // A continuación, simplemente lo dejamos en un valor ficticio por ahora
        double nuevoFactor = Math.random(); // Esto es solo un ejemplo; ajusta la lógica según el requerimiento.
        producto.setFactorDeRotacion(nuevoFactor);

        repository.guardar(producto);
    }
}
