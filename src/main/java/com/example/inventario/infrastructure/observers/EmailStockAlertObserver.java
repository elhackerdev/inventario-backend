package com.example.inventario.infrastructure.observers;

import com.example.inventario.domain.ports.observer.StockObserver;
import org.springframework.stereotype.Component;

@Component
public class EmailStockAlertObserver implements StockObserver {
    @Override
    public void notificarStockBajo(Long productoId, String nombreProducto, int stock) {
        System.out.printf("📧 Alerta por email: Stock bajo para '%s' (ID: %d). Stock actual: %d%n",
                nombreProducto, productoId, stock);

    }
}
