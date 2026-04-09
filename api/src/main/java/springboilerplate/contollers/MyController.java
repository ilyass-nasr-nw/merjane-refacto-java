package com.nimbleways.springboilerplate.contollers;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbleways.springboilerplate.dto.ApiResponse;
import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.services.OrderService;


@RestController
@RequestMapping("/orders")
public class MyController {

    private final OrderService orderService;

    public MyController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("{orderId}/processOrder")
    public ApiResponse<ProcessOrderResponse> processOrder(@PathVariable Long orderId) {
        ProcessOrderResponse processOrderResponse = orderService.processOrder(orderId);
        return ApiResponse.<ProcessOrderResponse>builder()
                .data(processOrderResponse)
                .message("Order processed successfully")
                .status(HttpStatus.OK)
                .build();
    }

}