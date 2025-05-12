package com.example.inventario.infrastructure.adapters.in.dto;

import com.example.inventario.domain.model.Movimiento;
import com.example.inventario.domain.model.Producto;

public class ResultadoOperacionDTO {

    private MovimientoDTO movimiento;
    private boolean stockBajo;
    private int cantidad;
    private String nombreProducto;

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public ResultadoOperacionDTO() {
    }

    public MovimientoDTO getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoDTO movimiento) {
        this.movimiento = movimiento;
    }

    public boolean isStockBajo() {
        return stockBajo;
    }

    public void setStockBajo(boolean stockBajo) {
        this.stockBajo = stockBajo;
    }
}
