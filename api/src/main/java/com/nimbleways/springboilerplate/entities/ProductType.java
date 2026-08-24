package com.nimbleways.springboilerplate.entities;

public enum ProductType {
    NORMAL("NORMAL"),
    SEASONAL("SEASONAL"),
    EXPIRABLE("EXPIRABLE");

    private final String code;

    ProductType(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
