package com.nimbleways.springboilerplate.dto.error;

import java.time.Instant;

/**
 * Standard error response returned by the API in case of failures.
 */
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
