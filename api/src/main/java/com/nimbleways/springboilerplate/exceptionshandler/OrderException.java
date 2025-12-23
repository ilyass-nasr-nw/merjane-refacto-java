package com.nimbleways.springboilerplate.exceptionshandler;

import lombok.Getter;

@Getter
public class OrderException extends RuntimeException {

	public OrderException(String message) {
		super(message);
	}
}
