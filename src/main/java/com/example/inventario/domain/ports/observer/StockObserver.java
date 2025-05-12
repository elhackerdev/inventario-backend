package com.example.inventario.domain.ports.observer;

public interface StockObserver {
    void notificarStockBajo(Long productoId, String nombreProducto, int stock);
}
