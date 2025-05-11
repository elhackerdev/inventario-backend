package com.example.inventario.infrastructure.config.mapper;

import com.example.inventario.domain.model.Movimiento;
import com.example.inventario.infrastructure.adapters.in.dto.MovimientoDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovimientoMapper {

    Movimiento dtoToEntity(MovimientoDTO dto);

    MovimientoDTO entityToDto(Movimiento movimiento);

    List<MovimientoDTO> entityListToDtoList(List<Movimiento> movimientos);

    Movimiento toEntity(Movimiento movimiento);

    Movimiento toDomain(Movimiento guardado);
}
