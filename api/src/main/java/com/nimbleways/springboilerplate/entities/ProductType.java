package com.nimbleways.springboilerplate.entities;

import java.util.Arrays;

public enum ProductType {
    NORMAL,
    SEASONAL,
    EXPIRABLE,
    UNKNOWN;

    public static ProductType from(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equals(value))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
