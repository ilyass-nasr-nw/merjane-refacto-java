package com.nimbleways.springboilerplate.config;

import com.nimbleways.springboilerplate.dto.error.ApiErrorResponse;
import com.nimbleways.springboilerplate.exceptions.BusinessException;
import com.nimbleways.springboilerplate.exceptions.TechnicalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.context.i18n.LocaleContextHolder;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(final BusinessException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessageCode());
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<ApiErrorResponse> handleTechnicalException(final TechnicalException exception) {
        log.error("Technical exception occurred", exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessageCode());
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(final HttpStatus status, final String messageCode) {
        var message = messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale());
        return ResponseEntity.status(status).body(new ApiErrorResponse(status.value(), message));
    }
}
