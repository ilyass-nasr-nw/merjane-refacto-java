package com.nimbleways.springboilerplate.exceptions;

import com.nimbleways.springboilerplate.dto.ErrorCode;
import com.nimbleways.springboilerplate.dto.ErrorMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {
    protected final ErrorCode errorCode;
    protected final HttpStatus httpStatus;
    protected final Object errorMetadata;

    protected BaseException(final ErrorCode errorCode, final HttpStatus httpStatus, final ErrorMessage message, final Object errorMetadata) {
        super(message.value());
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorMetadata = errorMetadata;
    }

    @Override
    public String toString() {
        return String.format("Exception(code=%s, message=%s, httpStatus=%s, metadata=%s)", this.getErrorCode(), this.getMessage(),
                this.getHttpStatus(), this.getErrorMetadata());
    }
}
