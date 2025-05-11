package com.example.inventario.domain.exceptions;

public class ProductoNotFoundException extends RuntimeException {

    // Constructor con un mensaje personalizado
    public ProductoNotFoundException(String mensaje) {
        super(mensaje);  // Llama al constructor de RuntimeException con el mensaje
    }

    // Constructor con un mensaje personalizado y la causa original de la excepción
    public ProductoNotFoundException(String mensaje, Throwable causa) {
        super(mensaje, causa);  // Llama al constructor de RuntimeException con mensaje y causa
    }
}
