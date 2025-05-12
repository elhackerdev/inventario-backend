package com.example.inventario.domain.ports.observer;

import java.util.ArrayList;
import java.util.List;

public class StockSubject {
    private List<StockObserver> observers = new ArrayList<>();

    public void agregarObserver(StockObserver observer) {
        observers.add(observer);
    }

    public void eliminarObserver(StockObserver observer) {
        observers.remove(observer);
    }

    public void notificarObservadores(Long productoId, String productoNombre, int stock) {
        for (StockObserver observer : observers) {
            observer.notificarStockBajo(productoId, productoNombre, stock);
        }
    }
}
