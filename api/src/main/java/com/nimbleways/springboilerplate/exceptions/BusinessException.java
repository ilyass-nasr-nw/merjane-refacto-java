package com.nimbleways.springboilerplate.exceptions;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String messageCode;

    public BusinessException(final String messageCode) {
        super(messageCode);
        this.messageCode = messageCode;
    }

    public BusinessException(final String messageCode, final Throwable cause) {
        super(messageCode, cause);
        this.messageCode = messageCode;
    }

}
