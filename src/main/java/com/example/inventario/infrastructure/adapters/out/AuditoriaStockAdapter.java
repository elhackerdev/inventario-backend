package com.example.inventario.infrastructure.adapters.out;

import com.example.inventario.domain.model.StockLog;
import com.example.inventario.domain.ports.out.RegistrarAuditoriaStockPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaStockAdapter implements RegistrarAuditoriaStockPort {

    @Autowired
    private StockLogRepository repository;

    @Override
    public void registrarLog(StockLog log) {
        StockLogEntity entity = new StockLogEntity();
        entity.setProductoId(log.getProductoId());
        entity.setCantidadAnterior(log.getCantidadAnterior());
        entity.setCantidadNueva(log.getCantidadNueva());
        entity.setOperacion(log.getOperacion());
        entity.setFecha(log.getFecha());

        repository.save(entity);
    }
}
