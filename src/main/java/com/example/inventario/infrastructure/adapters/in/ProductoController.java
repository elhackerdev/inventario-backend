package com.example.inventario.infrastructure.adapters.in;

import com.example.inventario.domain.exceptions.ProductoNotFoundException;
import com.example.inventario.domain.model.Producto;
import com.example.inventario.domain.ports.in.ProductoUseCase;
import com.example.inventario.infrastructure.config.mapper.ProductoMapper;
import com.example.inventario.infrastructure.adapters.in.dto.ProductoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Validated
public class ProductoController {

    private final ProductoUseCase productoUseCase;
    private final ProductoMapper mapper;

    public ProductoController(ProductoUseCase productoUseCase, ProductoMapper mapper) {
        this.productoUseCase = productoUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo producto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación o formato incorrecto")
    })
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoDTO dto) {

        Producto producto = mapper.dtoToEntity(dto);
        Producto creado = productoUseCase.crearProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.entityToDto(creado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un producto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        try {
            Producto producto = productoUseCase.obtenerProductoPorId(id);
            return ResponseEntity.ok(mapper.entityToDto(producto));
        } catch (ProductoNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todos los productos por nombre, categoria,codigo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Productos encontrados")
    })
    public ResponseEntity<List<ProductoDTO>> obtenerTodos(@RequestParam(required = false) String nombre,
                                                          @RequestParam(required = false) String categoria,
                                                          @RequestParam(required = false) String codigo) {
        List<Producto> productos = productoUseCase.buscarProductos(nombre, categoria, codigo);
        return ResponseEntity.ok(mapper.entitiesToDtos(productos));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto) {
        try {
            Producto producto = mapper.dtoToEntity(dto);
            Producto actualizado = productoUseCase.actualizarProducto(id, producto);
            return ResponseEntity.ok(mapper.entityToDto(actualizado));
        } catch (ProductoNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            productoUseCase.eliminarProducto(id);
            return ResponseEntity.noContent().build();
        } catch (ProductoNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
