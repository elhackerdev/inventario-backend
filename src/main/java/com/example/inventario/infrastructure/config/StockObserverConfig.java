package com.example.inventario.infrastructure.config;

import com.example.inventario.application.service.StockService;


import com.example.inventario.domain.ports.observer.StockSubject;
import com.example.inventario.infrastructure.adapters.out.NotificationService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;




@Configuration
public class StockObserverConfig {

    @Bean
    public StockService stockService() {
        StockSubject stockSubject = new StockSubject();
        NotificationService notificationService = new NotificationService();
        stockSubject.agregarObserver(notificationService);
        return new StockService(stockSubject, notificationService);
    }
}
