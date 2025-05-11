package com.example.inventario.infrastructure.adapters.out;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "producto",schema = "public")
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int stock;
    private String categoria;
    @Column(nullable = false, unique = true)
    private String codigo;
    private LocalDateTime fechaCreacion;
    private double factorDeRotacion;

    public ProductoEntity(Long id, String nombre, String descripcion, BigDecimal precio, int stock,
                    String categoria, String codigo, LocalDateTime fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.codigo = codigo;
        this.fechaCreacion = fechaCreacion;
        this.factorDeRotacion = 0.0;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();  // Establecer la fecha de creación si es nula
        }
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public double getFactorDeRotacion() {
        return factorDeRotacion;
    }

    public void setFactorDeRotacion(double factorDeRotacion) {
        this.factorDeRotacion = factorDeRotacion;
    }

    public void actualizarStock(int cantidad, boolean esEntrada) {
        if (esEntrada) {
            this.stock += cantidad;
        } else {
            if (this.stock < cantidad) {
                throw new IllegalArgumentException("Stock insuficiente para salida.");
            }
            this.stock -= cantidad;
        }
    }

    public void calcularFactorDeRotacion(int totalMovimientos, int periodoDias) {
        if (periodoDias == 0) {
            this.factorDeRotacion = 0;
            return;
        }
        this.factorDeRotacion = (double) totalMovimientos / periodoDias;
    }
}