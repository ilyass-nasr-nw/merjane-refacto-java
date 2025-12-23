package com.nimbleways.springboilerplate.contollers;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.services.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Order processing operations")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{orderId}/process")
    @Operation(
        summary = "Process an order",
        description = "Processes all products in an order, managing inventory and notifications based on product type"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order processed successfully",
            content = @Content(schema = @Schema(implementation = ProcessOrderResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid order ID",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found",
            content = @Content
        )
    })
    public ResponseEntity<ProcessOrderResponse> processOrder(
        @Parameter(description = "ID of the order to process", required = true)
        @PathVariable Long orderId
    ) {
        ProcessOrderResponse response = orderService.processOrder(orderId);
        return ResponseEntity.ok(response);
    }
}
