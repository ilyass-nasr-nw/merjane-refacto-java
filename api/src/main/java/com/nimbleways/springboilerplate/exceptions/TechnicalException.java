package com.nimbleways.springboilerplate.exceptions;

import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {

    private final String messageCode;

    public TechnicalException(final String messageCode) {
        super(messageCode);
        this.messageCode = messageCode;
    }

    public TechnicalException(final String messageCode, final Throwable cause) {
        super(messageCode, cause);
        this.messageCode = messageCode;
    }

}
