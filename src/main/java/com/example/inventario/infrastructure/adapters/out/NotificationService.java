package com.example.inventario.infrastructure.adapters.out;

import com.example.inventario.domain.ports.observer.StockObserver;

public class NotificationService implements StockObserver {

    @Override
    public void notificarStockBajo(Long productoId, String productoNombre, int stock) {
        // Implementación de la notificación, p. ej., enviar correo o mensaje
        System.out.println("Alerta: El producto " + productoNombre + " tiene un stock bajo de " + stock + " unidades.");
    }
}
