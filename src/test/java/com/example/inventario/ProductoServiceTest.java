package com.example.inventario;

import com.example.inventario.application.service.ProductoService;
import com.example.inventario.application.service.StockService;
import com.example.inventario.domain.exceptions.ProductoNotFoundException;
import com.example.inventario.domain.model.Producto;
import com.example.inventario.domain.ports.out.MovimientoRepositoryPort;
import com.example.inventario.domain.ports.out.ProductoRepositoryPort;
import com.example.inventario.infrastructure.adapters.in.dto.MovimientoDTO;
import com.example.inventario.infrastructure.adapters.in.dto.ResultadoOperacionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    @Mock
    private MovimientoRepositoryPort movimientoRepositoryPort;

    @Mock
    private StockService stockService;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;
    private final Long PRODUCTO_ID = 1L;

    @BeforeEach
    void setUp() {
        producto = new Producto(
                PRODUCTO_ID,
                "Tinta",
                "prueba",
                BigDecimal.valueOf(20000),
                5,
                "medias",
                "10001",
                LocalDateTime.now()
        );
    }

    @Test
    void crearProducto_ConCodigoUnico_DeberiaGuardarProducto() {
        when(productoRepositoryPort.buscarPorCodigo(anyString())).thenReturn(Optional.empty());
        when(productoRepositoryPort.guardar(any(Producto.class))).thenReturn(producto);

        Producto resultado = productoService.crearProducto(producto);

        assertNotNull(resultado);
        assertEquals(PRODUCTO_ID, resultado.getId());
        verify(productoRepositoryPort).guardar(producto);
    }

    @Test
    void crearProducto_ConCodigoExistente_DeberiaLanzarExcepcion() {
        when(productoRepositoryPort.buscarPorCodigo(producto.getCodigo())).thenReturn(Optional.of(producto));

        assertThrows(RuntimeException.class, () -> productoService.crearProducto(producto));
    }

    @Test
    void obtenerProductoPorId_Existente_DeberiaRetornarProducto() {
        when(productoRepositoryPort.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.obtenerProductoPorId(PRODUCTO_ID);

        assertNotNull(resultado);
        assertEquals(PRODUCTO_ID, resultado.getId());
    }

    @Test
    void obtenerProductoPorId_NoExistente_DeberiaLanzarExcepcion() {
        when(productoRepositoryPort.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.empty());

        assertThrows(ProductoNotFoundException.class, () -> productoService.obtenerProductoPorId(PRODUCTO_ID));
    }

    @Test
    void buscarProductos_ConCriterios_DeberiaRetornarLista() {
        when(productoRepositoryPort.buscarPorCriterios("Tinta", "medias", "10001"))
                .thenReturn(List.of(producto));

        List<Producto> resultados = productoService.buscarProductos("Tinta", "medias", "10001");

        assertFalse(resultados.isEmpty());
        assertEquals(1, resultados.size());
    }

    @Test
    void actualizarProducto_Existente_DeberiaActualizar() {
        Producto productoActualizado = new Producto(
                PRODUCTO_ID,
                "Tinta Actualizada",
                "prueba",
                BigDecimal.valueOf(25000),
                10,
                "medias",
                "10001",
                LocalDateTime.now()
        );

        when(productoRepositoryPort.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.of(producto));
        when(productoRepositoryPort.guardar(any(Producto.class))).thenReturn(productoActualizado);

        Producto resultado = productoService.actualizarProducto(PRODUCTO_ID, productoActualizado);

        assertEquals("Tinta Actualizada", resultado.getNombre());
        assertEquals(BigDecimal.valueOf(25000), resultado.getPrecio());
    }

    @Test
    void eliminarProducto_Existente_DeberiaEliminar() {
        when(productoRepositoryPort.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.of(producto));

        productoService.eliminarProducto(PRODUCTO_ID);

        verify(productoRepositoryPort).eliminarPorId(PRODUCTO_ID);
    }

    @Test
    void entradaStock_CantidadPositiva_DeberiaAumentarStock() {
        int cantidadEntrada = 5;
        when(productoRepositoryPort.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.of(producto));
        when(productoRepositoryPort.guardar(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto resultado = productoService.entradaStock(PRODUCTO_ID, cantidadEntrada);

        assertEquals(10, resultado.getStock()); // 5 inicial + 5 entrada
    }

   @Test
    void salidaStock_CantidadPositiva_DeberiaDisminuirStock() {
        int cantidadSalida = 3;
        when(productoRepositoryPort.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.of(producto));
        when(productoRepositoryPort.guardar(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto resultado = productoService.salidaStock(PRODUCTO_ID, cantidadSalida);

        assertEquals(2, resultado.getStock()); // 5 inicial - 3 salida
    }

    @Test
    void salidaStock_SinStockSuficiente_DeberiaLanzarExcepcion() {
        when(productoRepositoryPort.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.of(producto));

        assertThrows(IllegalArgumentException.class, () -> productoService.salidaStock(PRODUCTO_ID, 10));
    }

    @Test
    void recalcularFactorDeRotacion_ConDatosValidos_DeberiaCalcular() {
        when(productoRepositoryPort.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.of(producto));
        when(movimientoRepositoryPort.sumarCostoVentasPorProducto(PRODUCTO_ID)).thenReturn(1000.0);
        when(productoRepositoryPort.guardar(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto resultado = productoService.recalcularFactorDeRotacion(PRODUCTO_ID);

        assertTrue(resultado.getFactorDeRotacion() > 0);
    }
}
