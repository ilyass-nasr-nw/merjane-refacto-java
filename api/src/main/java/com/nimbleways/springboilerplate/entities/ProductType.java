package com.nimbleways.springboilerplate.entities;

public enum ProductType {
    NORMAL("NORMAL"),
    SEASONAL("SEASONAL"),
    EXPIRABLE("EXPIRABLE"),
    FLASHSALE("FLASHSALE");

    private final String value;

    ProductType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
