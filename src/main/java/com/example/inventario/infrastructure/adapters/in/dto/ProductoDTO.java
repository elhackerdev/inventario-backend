package com.example.inventario.infrastructure.adapters.in.dto;
import jakarta.persistence.Entity;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductoDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no debe exceder 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no debe exceder 255 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0")
    private BigDecimal precio;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private int stock;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20, message = "El código no debe exceder 20 caracteres")
    private String codigo;
    private LocalDateTime fechaCreacion;

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "El nombre es obligatorio") @Size(max = 100, message = "El nombre no debe exceder 100 caracteres") String getNombre() {
        return nombre;
    }

    public void setNombre(@NotBlank(message = "El nombre es obligatorio") @Size(max = 100, message = "El nombre no debe exceder 100 caracteres") String nombre) {
        this.nombre = nombre;
    }

    public @Size(max = 255, message = "La descripción no debe exceder 255 caracteres") String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(@Size(max = 255, message = "La descripción no debe exceder 255 caracteres") String descripcion) {
        this.descripcion = descripcion;
    }

    public @NotNull(message = "El precio es obligatorio") @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0") BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(@NotNull(message = "El precio es obligatorio") @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0") BigDecimal precio) {
        this.precio = precio;
    }

    @Min(value = 0, message = "El stock no puede ser negativo")
    public int getStock() {
        return stock;
    }

    public void setStock(@Min(value = 0, message = "El stock no puede ser negativo") int stock) {
        this.stock = stock;
    }

    public @NotBlank(message = "La categoría es obligatoria") String getCategoria() {
        return categoria;
    }

    public void setCategoria(@NotBlank(message = "La categoría es obligatoria") String categoria) {
        this.categoria = categoria;
    }

    public @NotBlank(message = "El código es obligatorio") @Size(max = 20, message = "El código no debe exceder 20 caracteres") String getCodigo() {
        return codigo;
    }

    public void setCodigo(@NotBlank(message = "El código es obligatorio") @Size(max = 20, message = "El código no debe exceder 20 caracteres") String codigo) {
        this.codigo = codigo;
    }
}
