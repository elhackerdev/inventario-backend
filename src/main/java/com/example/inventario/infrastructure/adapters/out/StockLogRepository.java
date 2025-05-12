package com.example.inventario.infrastructure.adapters.out;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockLogRepository extends JpaRepository<StockLogEntity, Long> {}
