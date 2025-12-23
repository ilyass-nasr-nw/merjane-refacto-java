package com.nimbleways.springboilerplate.contollers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.services.implementations.OrderService;
import com.nimbleways.springboilerplate.services.implementations.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderEndpoint {
	private final ProductService productService;
	private final OrderService orderService;

	@PostMapping("{orderId}/process")
	public ResponseEntity<ProcessOrderResponse> processOrder(@PathVariable Long orderId) {
		Order order = orderService.getOrderById(orderId);
		order.getItems().forEach(productService::handleProduct);
		ProcessOrderResponse response = new ProcessOrderResponse(order.getId());
		return ResponseEntity.ok(response);
	}

}
