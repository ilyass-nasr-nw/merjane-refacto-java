package com.nimbleways.springboilerplate.contollers;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.services.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * REST controller responsible for order operations.
 */
@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * Creates a new {@link OrderController}.
     *
     * @param orderService service responsible for processing orders
     */
    public OrderController(OrderService orderService) {
        this.orderService = Objects.requireNonNull(orderService, "orderService must not be null");
    }

    /**
     * Processes an order by its identifier.
     *
     * <p>
     * Any exception raised during processing is intentionally not handled here.
     * It is delegated to the global {@code @RestControllerAdvice}.
     * </p>
     *
     * @param orderId order identifier
     * @return response containing the processed order id
     */
    @PostMapping("{orderId}/processOrder")
    @ResponseStatus(HttpStatus.OK)
    public ProcessOrderResponse processOrder(@PathVariable Long orderId) {

        log.info("Order processing requested. orderId={}", orderId);

        orderService.processOrder(orderId);

        log.info("Order processed successfully. orderId={}", orderId);
        return new ProcessOrderResponse(orderId);
    }
}
