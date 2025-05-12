package com.example.inventario;

import com.example.inventario.application.service.MovimientoService;
import com.example.inventario.application.service.ProductoService;
import com.example.inventario.domain.exceptions.InvalidStockException;

import com.example.inventario.domain.exceptions.ProductoNotFoundException;
import com.example.inventario.domain.model.Movimiento;
import com.example.inventario.domain.model.Producto;
import com.example.inventario.domain.ports.out.MovimientoRepositoryPort;
import com.example.inventario.domain.ports.out.ProductoRepositoryPort;
import com.example.inventario.infrastructure.adapters.out.MovimientoEntity;
import com.example.inventario.infrastructure.config.mapper.MovimientoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepositoryPort movimientoRepository;

    @Mock
    private ProductoRepositoryPort productoRepository;

    @Mock
    private ProductoService productoService;

    @Mock
    private MovimientoMapper mapper;

    @InjectMocks
    private MovimientoService movimientoService;

    private Producto producto;
    private Movimiento movimientoEntrada;
    private Movimiento movimientoSalida;
    private final Long PRODUCTO_ID = 1L;
    private final Long MOVIMIENTO_ID = 1L;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(PRODUCTO_ID);
        producto.setStock(10);

        movimientoEntrada = new Movimiento();
        movimientoEntrada.setId(MOVIMIENTO_ID);
        movimientoEntrada.setProducto(producto);
        movimientoEntrada.setCantidad(5);
        movimientoEntrada.setTipo(Movimiento.TipoMovimiento.ENTRADA);

        movimientoSalida = new Movimiento();
        movimientoSalida.setId(MOVIMIENTO_ID);
        movimientoSalida.setProducto(producto);
        movimientoSalida.setCantidad(3);
        movimientoSalida.setTipo(Movimiento.TipoMovimiento.SALIDA);
    }

    @Test
    void crearMovimiento_Entrada_DeberiaAumentarStock() {
        when(productoRepository.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.of(producto));
        when(movimientoRepository.guardar(any(Movimiento.class))).thenReturn(movimientoEntrada);

        Movimiento resultado = movimientoService.crearMovimiento(movimientoEntrada);

        assertNotNull(resultado);
        verify(productoService).entradaStock(PRODUCTO_ID, 5);
        verify(movimientoRepository).guardar(movimientoEntrada);
    }

    @Test
    void crearMovimiento_Salida_DeberiaDisminuirStock() {
        when(productoRepository.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.of(producto));
        when(movimientoRepository.guardar(any(Movimiento.class))).thenReturn(movimientoSalida);

        Movimiento resultado = movimientoService.crearMovimiento(movimientoSalida);

        assertNotNull(resultado);
        verify(productoService).salidaStock(PRODUCTO_ID, 3);
        verify(movimientoRepository).guardar(movimientoSalida);
    }

    @Test
    void crearMovimiento_ProductoNoExistente_DeberiaLanzarExcepcion() {
        when(productoRepository.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.empty());

        assertThrows(ProductoNotFoundException.class, () ->
                movimientoService.crearMovimiento(movimientoEntrada)
        );
    }

    @Test
    void obtenerMovimientoPorId_Existente_DeberiaRetornarMovimiento() {
        when(movimientoRepository.buscarPorId(MOVIMIENTO_ID)).thenReturn(Optional.of(movimientoEntrada));

        Movimiento resultado = movimientoService.obtenerMovimientoPorId(MOVIMIENTO_ID);

        assertNotNull(resultado);
        assertEquals(MOVIMIENTO_ID, resultado.getId());
    }

    @Test
    void obtenerMovimientoPorId_NoExistente_DeberiaLanzarExcepcion() {
        when(movimientoRepository.buscarPorId(MOVIMIENTO_ID)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                movimientoService.obtenerMovimientoPorId(MOVIMIENTO_ID)
        );
    }

    @Test
    void registrarMovimiento_Entrada_DeberiaAumentarStock() {
        when(productoRepository.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.of(producto));
        when(movimientoRepository.guardar(any(Movimiento.class))).thenReturn(movimientoEntrada);

        Movimiento resultado = movimientoService.registrarMovimiento(movimientoEntrada);

        assertNotNull(resultado);
        verify(productoService).entradaStock(PRODUCTO_ID, 5);
        verify(movimientoRepository).guardar(movimientoEntrada);
    }

    @Test
    void obtenerMovimientosPorProducto_DeberiaRetornarLista() {
        when(movimientoRepository.buscarPorProductoId(PRODUCTO_ID))
                .thenReturn(Arrays.asList(movimientoEntrada, movimientoSalida));

        List<Movimiento> resultados = movimientoService.obtenerMovimientosPorProducto(PRODUCTO_ID);

        assertEquals(2, resultados.size());
    }

    @Test
    void obtenerTodosLosMovimientos_DeberiaRetornarLista() {
        when(movimientoRepository.obtenerTodos())
                .thenReturn(Arrays.asList(movimientoEntrada, movimientoSalida));

        List<Movimiento> resultados = movimientoService.obtenerTodosLosMovimientos();

        assertEquals(2, resultados.size());
    }

    @Test
    void buscarMovimientos_PorProductoYTipo_DeberiaRetornarLista() {
        MovimientoEntity entity = new MovimientoEntity();
        when(movimientoRepository.findByProductoIdAndTipo(PRODUCTO_ID, Movimiento.TipoMovimiento.ENTRADA))
                .thenReturn(Arrays.asList(entity));
        when(mapper.toDomain(entity)).thenReturn(movimientoEntrada);

        List<Movimiento> resultados = movimientoService.buscarMovimientos(PRODUCTO_ID, "entrada");

        assertEquals(1, resultados.size());
        assertEquals(Movimiento.TipoMovimiento.ENTRADA, resultados.get(0).getTipo());
    }

    @Test
    void eliminarMovimiento_Entrada_DeberiaDisminuirStock() {
        when(movimientoRepository.buscarPorId(MOVIMIENTO_ID)).thenReturn(Optional.of(movimientoEntrada));
        when(productoRepository.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.of(producto));

        movimientoService.eliminarMovimiento(MOVIMIENTO_ID);

        verify(productoService).salidaStock(PRODUCTO_ID, 5);
        verify(movimientoRepository).eliminarPorId(MOVIMIENTO_ID);
    }

    @Test
    void actualizarMovimiento_SalidaConStockInsuficiente_DeberiaLanzarExcepcion() {
        Movimiento movimientoActualizado = new Movimiento();
        movimientoActualizado.setCantidad(20); // Más que el stock disponible
        movimientoActualizado.setTipo(Movimiento.TipoMovimiento.SALIDA);

        when(movimientoRepository.buscarPorId(MOVIMIENTO_ID)).thenReturn(Optional.of(movimientoEntrada));
        when(productoRepository.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.of(producto));

        assertThrows(InvalidStockException.class, () ->
                movimientoService.actualizarMovimiento(MOVIMIENTO_ID, movimientoActualizado)
        );
    }

    @Test
    void recalcularFactorDeRotacion_ConMovimientos_DeberiaCalcular() {
        when(productoRepository.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.of(producto));
        when(movimientoRepository.buscarPorProductoId(PRODUCTO_ID))
                .thenReturn(Arrays.asList(movimientoSalida, movimientoSalida)); // 2 salidas de 3 = 6 total

        movimientoService.recalcularFactorDeRotacion(PRODUCTO_ID);

        // 6 (total vendido) / 10 (stock actual) = 0.6
        verify(productoRepository).guardar(argThat(p ->
                p.getFactorDeRotacion() == 0.6
        ));
    }
}
