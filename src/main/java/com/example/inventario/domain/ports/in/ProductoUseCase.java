package com.example.inventario.domain.ports.in;

import com.example.inventario.domain.model.Producto;

import java.util.List;

public interface ProductoUseCase {
    // CRUD
    Producto crearProducto(Producto producto);

    Producto obtenerProductoPorId(Long id);

    List<Producto> buscarProductos(String nombre, String categoria, String codigo);

    Producto actualizarProducto(Long id, Producto producto);

    void eliminarProducto(Long id);

    // Gestión de stock
    Producto entradaStock(Long idProducto, int cantidad);

    Producto salidaStock(Long idProducto, int cantidad);

    // Verificación y lógica de negocio
    void verificarStockMinimo(Long idProducto);

    void recalcularFactorDeRotacion(Long idProducto);
}
