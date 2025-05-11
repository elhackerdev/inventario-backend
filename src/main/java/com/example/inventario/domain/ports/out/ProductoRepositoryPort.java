package com.example.inventario.domain.ports.out;

import com.example.inventario.domain.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepositoryPort {

    Producto guardar(Producto producto);

    Optional<Producto> buscarPorId(Long id);

    Optional<Producto> buscarPorCodigo(String codigo);

    List<Producto> buscarPorCriterios(String nombre, String categoria, String codigo);

    List<Producto> obtenerTodos();

    void eliminarPorId(Long id);
}
