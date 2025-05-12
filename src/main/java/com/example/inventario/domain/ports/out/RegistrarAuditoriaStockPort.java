package com.example.inventario.domain.ports.out;

import com.example.inventario.domain.model.StockLog;

public interface RegistrarAuditoriaStockPort {
    void registrarLog(StockLog log);
}
