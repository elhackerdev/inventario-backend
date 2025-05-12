package com.example.inventario.domain.ports.in;

import com.example.inventario.domain.model.Producto;
import com.example.inventario.infrastructure.adapters.in.dto.MovimientoDTO;
import com.example.inventario.infrastructure.adapters.in.dto.ResultadoOperacionDTO;

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
    ResultadoOperacionDTO verificarStockMinimo(Long idProducto, MovimientoDTO movimiento);

    Producto recalcularFactorDeRotacion(Long idProducto);
}
