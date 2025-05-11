package com.example.inventario.infrastructure.adapters.in;

import com.example.inventario.domain.model.Movimiento;
import com.example.inventario.domain.ports.in.MovimientoUseCase;
import com.example.inventario.infrastructure.config.mapper.MovimientoMapper;
import com.example.inventario.infrastructure.adapters.in.dto.MovimientoDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    private final MovimientoUseCase movimientoUseCase;
    private final MovimientoMapper mapper;

    public MovimientoController(MovimientoUseCase movimientoUseCase, MovimientoMapper mapper) {
        this.movimientoUseCase = movimientoUseCase;
        this.mapper = mapper;
    }

    // Crear un movimiento (entrada o salida)
    @PostMapping
    public ResponseEntity<MovimientoDTO> crearMovimiento(@Valid @RequestBody MovimientoDTO dto) {
        Movimiento movimiento = mapper.dtoToEntity(dto);
        Movimiento creado = movimientoUseCase.crearMovimiento(movimiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.entityToDto(creado));
    }

    // Obtener un movimiento por ID
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDTO> obtenerMovimientoPorId(@PathVariable Long id) {
        Movimiento movimiento = movimientoUseCase.obtenerMovimientoPorId(id);
        return ResponseEntity.ok(mapper.entityToDto(movimiento));
    }

    // Buscar movimientos por producto (entrada/salida)
    @GetMapping
    public ResponseEntity<List<MovimientoDTO>> buscarMovimientos(
            @RequestParam(required = false) Long productoId,
            @RequestParam(required = false) String tipo) {

        List<Movimiento> movimientos = movimientoUseCase.buscarMovimientos(productoId, tipo);
        return ResponseEntity.ok(mapper.entityListToDtoList(movimientos));
    }

    // Actualizar un movimiento
    @PutMapping("/{id}")
    public ResponseEntity<MovimientoDTO> actualizarMovimiento(
            @PathVariable Long id, @Valid @RequestBody MovimientoDTO dto) {

        Movimiento movimiento = mapper.dtoToEntity(dto);
        Movimiento actualizado = movimientoUseCase.actualizarMovimiento(id, movimiento);
        return ResponseEntity.ok(mapper.entityToDto(actualizado));
    }

    // Eliminar un movimiento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        movimientoUseCase.eliminarMovimiento(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
