package com.example.inventario.domain.model;

import java.time.LocalDateTime;

public class Movimiento {
    public enum TipoMovimiento {
        ENTRADA,
        SALIDA
    }

    private Long id;
    private Long idProducto;
    private TipoMovimiento tipo;
    private int cantidad;
    private LocalDateTime fecha;
    private String descripcion;

    public Movimiento(Long id, Long idProducto, TipoMovimiento tipo, int cantidad,
                      LocalDateTime fecha, String descripcion) {
        this.id = id;
        this.idProducto = idProducto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
