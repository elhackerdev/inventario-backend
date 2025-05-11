package com.example.inventario.infrastructure.config.mapper;

import com.example.inventario.domain.model.Producto;
import com.example.inventario.infrastructure.adapters.in.dto.ProductoDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    Producto dtoToEntity(ProductoDTO dto);
    ProductoDTO entityToDto(Producto producto);
    Producto toEntity(Producto producto);

    Producto toDomain(Producto productoEntity);
}
