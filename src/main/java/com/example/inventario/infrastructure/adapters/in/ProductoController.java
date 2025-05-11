package com.example.inventario.infrastructure.adapters.in;

import com.example.inventario.domain.exceptions.ProductoNotFoundException;
import com.example.inventario.domain.model.Producto;
import com.example.inventario.domain.ports.in.ProductoUseCase;
import com.example.inventario.infrastructure.config.mapper.ProductoMapper;
import com.example.inventario.infrastructure.adapters.in.dto.ProductoDTO;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Api(tags = "Productos")
public class ProductoController {

    private final ProductoUseCase productoUseCase;
    private final ProductoMapper mapper;

    public ProductoController(ProductoUseCase productoUseCase, ProductoMapper mapper) {
        this.productoUseCase = productoUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @ApiOperation(value = "Crear un nuevo producto", response = ProductoDTO.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "Producto creado exitosamente"),
            @ApiResponse(code = 400, message = "Error de validación o formato incorrecto")
    })
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoDTO dto) {
        Producto producto = mapper.dtoToEntity(dto);
        Producto creado = productoUseCase.crearProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.entityToDto(creado));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Obtener un producto por ID", response = ProductoDTO.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Producto encontrado"),
            @ApiResponse(code = 404, message = "Producto no encontrado")
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
    @ApiOperation(value = "Obtener todos los productos", response = List.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Productos encontrados")
    })
    public ResponseEntity<List<ProductoDTO>> obtenerTodos() {
        List<Producto> productos = productoUseCase.buscarProductos(null, null, null);
        return ResponseEntity.ok(mapper.entitiesToDtos(productos));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Actualizar un producto", response = ProductoDTO.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Producto actualizado exitosamente"),
            @ApiResponse(code = 404, message = "Producto no encontrado")
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
    @ApiOperation(value = "Eliminar un producto")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Producto eliminado exitosamente"),
            @ApiResponse(code = 404, message = "Producto no encontrado")
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
