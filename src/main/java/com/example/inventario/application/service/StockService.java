package com.example.inventario.application.service;

import com.example.inventario.domain.model.Producto;
import com.example.inventario.domain.ports.observer.StockSubject;
import com.example.inventario.infrastructure.adapters.out.NotificationService;

public class StockService {

    private StockSubject stockSubject;
    private NotificationService notificationService;

    public StockService(StockSubject stockSubject, NotificationService notificationService) {
        this.stockSubject = stockSubject;
        this.notificationService = notificationService;
    }

    public void verificarStockBajo(Producto producto, int umbral) {
        if (producto.esStockBajo(umbral)) {
            stockSubject.notificarObservadores(producto.getId(), producto.getNombre(), producto.getStock());
        }
    }
}
