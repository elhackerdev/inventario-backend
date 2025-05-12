package com.example.inventario;

import com.example.inventario.application.service.StockService;
import com.example.inventario.domain.model.Producto;
import com.example.inventario.domain.ports.observer.StockSubject;
import com.example.inventario.infrastructure.adapters.out.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockSubject stockSubject;

    @Mock
    private NotificationService notificationService;

    private StockService stockService;

    @BeforeEach
    void setUp() {
        stockService = new StockService(stockSubject, notificationService);
    }

    @Test
    void verificarStockBajo_CuandoStockEsBajo_DeberiaNotificarObservadores() {
        // Arrange
        Producto producto = new Producto(1L, "Tinta", "prueba", BigDecimal.valueOf(20000), 9, "medias", "10001", LocalDateTime.now());
        int umbral = 10;

        // Act
        stockService.verificarStockBajo(producto, umbral);

        // Assert
        verify(stockSubject).notificarObservadores(1L, "Tinta", 9);
    }

    @Test
    void verificarStockBajo_CuandoStockNoEsBajo_NoDeberiaNotificarObservadores() {
        // Arrange
        Producto producto = new Producto(1L, "Tinta", "prueba", BigDecimal.valueOf(20000), 10, "medias", "10001", LocalDateTime.now());
        int umbral = 10;

        // Act
        stockService.verificarStockBajo(producto, umbral);

        // Assert
        verify(stockSubject, never()).notificarObservadores(anyLong(), anyString(), anyInt());
    }

    @Test
    void verificarStockBajo_CuandoStockEsIgualAlUmbral_NoDeberiaNotificarObservadores() {
        // Arrange
        Producto producto = new Producto(1L, "Tinta", "prueba", BigDecimal.valueOf(20000), 10, "medias", "10001", LocalDateTime.now());        int umbral = 10;

        // Act
        stockService.verificarStockBajo(producto, umbral);

        // Assert
        verify(stockSubject, never()).notificarObservadores(anyLong(), anyString(), anyInt());
    }
}