package com.example.inventario.infrastructure.adapters.out;

import com.example.inventario.domain.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar productos por nombre (cualquier parte del nombre)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Buscar productos por categoría
    List<Producto> findByCategoria(String categoria);

    // Buscar productos por código
    Optional<Producto> findByCodigo(String codigo);

    // Búsqueda por combinación de criterios
    List<Producto> findByNombreContainingIgnoreCaseAndCategoriaAndCodigo(String nombre, String categoria, String codigo);

    @Query(value = "SELECT * FROM public.buscar_productos(:p_nombre, :p_categoria, :p_codigo)", nativeQuery = true)
    List<Producto> buscar_productos(@Param("p_nombre") String nombre,@Param("p_categoria") String categoria,@Param("p_codigo") String codigo);
}
