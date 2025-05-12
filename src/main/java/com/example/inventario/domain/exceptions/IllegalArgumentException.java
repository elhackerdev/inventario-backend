package com.example.inventario.domain.exceptions;

public class IllegalArgumentException extends RuntimeException {

    public IllegalArgumentException(String mensaje) {
        super(mensaje);
    }

    public IllegalArgumentException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}