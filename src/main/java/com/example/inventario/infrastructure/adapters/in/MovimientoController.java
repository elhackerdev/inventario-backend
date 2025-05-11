package com.example.inventario.infrastructure.adapters.in;

import com.example.inventario.domain.model.Movimiento;
import com.example.inventario.domain.ports.in.MovimientoUseCase;
import com.example.inventario.infrastructure.adapters.out.MovimientoEntity;
import com.example.inventario.infrastructure.config.mapper.MovimientoMapper;
import com.example.inventario.infrastructure.adapters.in.dto.MovimientoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Crear un nuevo movimiento", description = "Crea un nuevo movimiento de entrada o salida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movimiento creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<MovimientoDTO> crearMovimiento(@Valid @RequestBody MovimientoDTO dto) {
        Movimiento movimiento = mapper.dtoToDomain(dto);
        Movimiento creado = movimientoUseCase.crearMovimiento(movimiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.domainToDto(creado));
    }

    @Operation(summary = "Obtener un movimiento por ID", description = "Devuelve el movimiento correspondiente al ID dado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDTO> obtenerMovimientoPorId(
            @Parameter(description = "ID del movimiento") @PathVariable Long id) {
        Movimiento movimiento = movimientoUseCase.obtenerMovimientoPorId(id);
        return ResponseEntity.ok(mapper.domainToDto(movimiento));
    }

    @Operation(summary = "Buscar movimientos", description = "Buscar movimientos por producto y tipo (opcional)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de movimientos encontrados")
    })
    @GetMapping
    public ResponseEntity<List<MovimientoDTO>> buscarMovimientos(
            @Parameter(description = "ID del producto") @RequestParam(required = false) Long productoId,
            @Parameter(description = "Tipo de movimiento: ENTRADA o SALIDA") @RequestParam(required = false) String tipo) {

        List<Movimiento> movimientos = movimientoUseCase.buscarMovimientos(productoId, tipo);
        return ResponseEntity.ok(mapper.domainListToDtoList(movimientos));
    }

    @Operation(summary = "Actualizar un movimiento", description = "Actualiza un movimiento existente por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MovimientoDTO> actualizarMovimiento(
            @Parameter(description = "ID del movimiento a actualizar") @PathVariable Long id,
            @Valid @RequestBody MovimientoDTO dto) {

        Movimiento movimiento = mapper.dtoToDomain(dto);
        Movimiento actualizado = movimientoUseCase.actualizarMovimiento(id, movimiento);
        return ResponseEntity.ok(mapper.domainToDto(actualizado));
    }

    @Operation(summary = "Eliminar un movimiento", description = "Elimina un movimiento por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movimiento eliminado"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(
            @Parameter(description = "ID del movimiento a eliminar") @PathVariable Long id) {
        movimientoUseCase.eliminarMovimiento(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
