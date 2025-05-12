package com.example.inventario.application.service;

import com.example.inventario.domain.model.Producto;
import com.example.inventario.domain.ports.observer.StockObserver;
import com.example.inventario.domain.ports.observer.StockSubject;
import com.example.inventario.infrastructure.adapters.out.NotificationService;

import java.util.List;

/**
 * Servicio encargado de gestionar la verificación de stock bajo
 * y de notificar a los observadores cuando sea necesario.
 *
 * Este servicio sigue el patrón Observer para alertar cambios importantes.
 */
public class StockService {

    // Sujeto que notifica a los observadores en caso de eventos de stock
    private StockSubject stockSubject;

    // Servicio encargado de enviar notificaciones (correo, SMS, etc.)
    private NotificationService notificationService;

    /**
     * Constructor que inyecta las dependencias necesarias.
     *
     * @param stockSubject objeto que gestiona los observadores
     * @param notificationService servicio de notificaciones
     */
    public StockService(StockSubject stockSubject, NotificationService notificationService) {
        this.stockSubject = stockSubject;
        this.notificationService = notificationService;
    }

    /**
     * Verifica si el stock de un producto está por debajo de un umbral determinado.
     * Si el stock es bajo, se dispara una notificación a los observadores registrados.
     *
     * @param producto el producto a evaluar
     * @param umbral el valor mínimo aceptable de stock
     */
    public void verificarStockBajo(Producto producto, int umbral) {
        if (producto.esStockBajo(umbral)) {
            stockSubject.notificarObservadores(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getStock()
            );
        }
    }
}
