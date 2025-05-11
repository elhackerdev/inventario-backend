package com.example.inventario.domain.exceptions;

public class MovimientoNotFoundException extends RuntimeException {

    public MovimientoNotFoundException(String mensaje) {
        super(mensaje);
    }
}