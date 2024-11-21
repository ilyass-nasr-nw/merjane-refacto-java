package com.nimbleways.springboilerplate.dto;

public record ErrorMessage(String value) {
    public static ErrorMessage from(final String value) {
        return new ErrorMessage(value);
    }

}
