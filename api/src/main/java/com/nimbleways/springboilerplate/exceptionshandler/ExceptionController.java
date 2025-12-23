package com.nimbleways.springboilerplate.exceptionshandler;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionController {
	@ExceptionHandler(ProductException.class)
	public ResponseEntity<Map<String, Object>> productNotFoundException(ProductException ex, WebRequest request) {
		Map<String, Object> body = new HashMap<>();
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", "Product Not Found Exception");
		body.put("message", ex.getMessage());
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(OrderException.class)
	public ResponseEntity<Map<String, Object>> orderNotFoundException(OrderException ex, WebRequest request) {
		Map<String, Object> body = new HashMap<>();
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", "Order Not Found Exception");
		body.put("message", ex.getMessage());
		return ResponseEntity.badRequest().body(body);
	}

}
