package com.nimbleways.springboilerplate.contollers;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.services.implementations.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class MyController {
    private OrderService orderService;

    @PostMapping("{orderId}/processOrder")
    @ResponseStatus(HttpStatus.OK)
    public ProcessOrderResponse processOrder(@PathVariable Long orderId) {
        return orderService.processOrder(orderId);

    }
}
