package com.nimbleways.springboilerplate.contollers;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.services.order.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderApi {
    private final OrderService orderService;

    @PostMapping("{orderId}/processOrder")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ProcessOrderResponse> processOrder(@PathVariable Long orderId) {
        try {
            log.info("Start processing order {}", orderId);
            ProcessOrderResponse processedOrder = orderService.processOrder(orderId);
            log.info("End processing order {}", orderId);
            return ResponseEntity.ok(new ProcessOrderResponse(processedOrder.id()));
        } catch (OrderNotFoundException exception) {
            log.error("error processing order {}", orderId, exception);
            return ResponseEntity.notFound().build();
        }
    }
}
