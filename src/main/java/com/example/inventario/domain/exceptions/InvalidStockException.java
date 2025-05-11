package com.example.inventario.domain.exceptions;

public class InvalidStockException extends RuntimeException {

    public InvalidStockException(String mensaje) {
        super(mensaje);
    }

    public InvalidStockException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
