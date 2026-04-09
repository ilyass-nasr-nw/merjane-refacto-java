package com.nimbleways.springboilerplate.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;


@Builder
public class ApiResponse<T> {
    private T data;
    private String message;
    private HttpStatus status;

    public ApiResponse() {
    }

    public ApiResponse(T data, String message, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public HttpStatus getStatus() {
        return status;
    }
  
    public String getMessage() {
        return message;
    
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
}