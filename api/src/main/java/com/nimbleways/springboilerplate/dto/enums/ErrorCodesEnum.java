package com.nimbleways.springboilerplate.dto.enums;

public enum ErrorCodesEnum {

    ORDER_NOT_FOUND("order_not_found");

    private final String errorCode;

    ErrorCodesEnum(final String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return errorCode;
    }
}
