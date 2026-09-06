package com.nimbleways.springboilerplate.services.implementations.exceptions;

public class UnsupportedProductTypeException extends RuntimeException {
    public UnsupportedProductTypeException(String productType) {
        super("Unsupported Product Type : " + productType);
    }
}
