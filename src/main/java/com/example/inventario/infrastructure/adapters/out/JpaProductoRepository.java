package com.example.inventario.infrastructure.adapters.out;

import com.example.inventario.domain.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar productos por nombre (cualquier parte del nombre)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Buscar productos por categoría
    List<Producto> findByCategoria(String categoria);

    // Buscar productos por código
    List<Producto> findByCodigo(String codigo);

    // Búsqueda por combinación de criterios
    List<Producto> findByNombreContainingIgnoreCaseAndCategoriaAndCodigo(String nombre, String categoria, String codigo);
}
