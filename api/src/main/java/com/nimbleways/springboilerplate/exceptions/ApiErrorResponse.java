package com.nimbleways.springboilerplate.exceptions;

public record ApiErrorResponse(int status, String error, String message) {
}