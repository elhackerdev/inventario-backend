package com.example.inventario.infrastructure.config.mapper;

import com.example.inventario.domain.model.Movimiento;
import com.example.inventario.domain.model.Producto;
import com.example.inventario.infrastructure.adapters.in.dto.MovimientoDTO;
import com.example.inventario.infrastructure.adapters.out.MovimientoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


import java.util.List;

@Mapper(componentModel = "spring")
public interface MovimientoMapper {

    // ======== Entity <-> Domain ========
    Movimiento toDomain(MovimientoEntity entity);
    MovimientoEntity toEntity(Movimiento domain);


    // ======== DTO -> Domain ========
    @Mapping(source = "idProducto", target = "producto", qualifiedByName = "mapProductoFromId")
    Movimiento dtoToDomain(MovimientoDTO dto);

    // ======== Domain -> DTO ========
    @Mapping(source = "producto.id", target = "idProducto")
    @Mapping(source = "producto.nombre", target = "nombreProducto")
    MovimientoDTO domainToDto(Movimiento movimiento);

    List<MovimientoDTO> domainListToDtoList(List<Movimiento> movimientos);

    // ======== Mapper auxiliar ========
    @Named("mapProductoFromId")
    default Producto mapProductoFromId(Long id) {
        if (id == null) return null;
        Producto producto = new Producto();
        producto.setId(id);
        return producto;
    }
}
