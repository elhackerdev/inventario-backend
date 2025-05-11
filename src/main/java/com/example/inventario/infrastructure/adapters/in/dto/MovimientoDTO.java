package com.example.inventario.infrastructure.adapters.in.dto;
import jakarta.validation.constraints.*;
public class MovimientoDTO {

    private Long id;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long idProducto;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private String tipo;  // ENTRADA o SALIDA

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private int cantidad;

    @Size(max = 255, message = "La descripción no debe exceder 255 caracteres")
    private String descripcion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull(message = "El ID del producto es obligatorio") Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(@NotNull(message = "El ID del producto es obligatorio") Long idProducto) {
        this.idProducto = idProducto;
    }

    public @NotNull(message = "El tipo de movimiento es obligatorio") String getTipo() {
        return tipo;
    }

    public void setTipo(@NotNull(message = "El tipo de movimiento es obligatorio") String tipo) {
        this.tipo = tipo;
    }

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(@Min(value = 1, message = "La cantidad debe ser al menos 1") int cantidad) {
        this.cantidad = cantidad;
    }

    public @Size(max = 255, message = "La descripción no debe exceder 255 caracteres") String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(@Size(max = 255, message = "La descripción no debe exceder 255 caracteres") String descripcion) {
        this.descripcion = descripcion;
    }
}
