package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.OrderService;
import com.nimbleways.springboilerplate.services.ProductProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Service responsible for processing orders.
 * <p>
 * It loads an {@link Order} by its id, then applies the stock-management rules
 * for each {@link Product} by delegating the processing to the corresponding {@link ProductProcessor}.
 * </p>
 *
 * <p>
 * This implementation uses a "strategy" approach: each product type has its own processor.
 * This keeps the code compliant with SRP and makes it easy to add new product types
 * without modifying this service.
 * </p>
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final List<ProductProcessor> processors;

    /**
     * Creates an instance of {@link OrderServiceImpl}.
     *
     * @param orderRepository repository used to load orders
     * @param processors      available product processors (one per product type)
     */
    public OrderServiceImpl(OrderRepository orderRepository, List<ProductProcessor> processors) {
        this.orderRepository = Objects.requireNonNull(orderRepository, "orderRepository must not be null");
        this.processors = Objects.requireNonNull(processors, "processors must not be null");
    }

    /**
     * Processes an order by applying stock update rules for each product in the order.
     *
     * <p>
     * The method is transactional to ensure that inventory updates and persistence
     * operations are executed atomically for the whole order processing.
     * </p>
     *
     * @param orderId the order identifier
     * @throws IllegalArgumentException if {@code orderId} is null or if the order does not exist
     * @throws IllegalStateException    if a product type is not supported by any registered processor
     */
    @Override
    @Transactional
    public void processOrder(Long orderId) {

        if (orderId == null) {
            log.warn("Order processing rejected: orderId is null");
            throw new IllegalArgumentException("orderId must not be null");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found. orderId={}", orderId);
                    return new IllegalArgumentException("Order not found: " + orderId);
                });


        for (Product product : order.getItems()) {
            ProductProcessor processor = resolveProcessor(product);
            processor.process(product);
        }

    }


    /**
     * Finds the suitable processor for a given product.
     *
     * @param product the product to process
     * @return the processor supporting this product
     * @throws IllegalStateException if no processor supports the product
     */
    private ProductProcessor resolveProcessor(Product product) {
        if (product == null) {
            log.error("No processor found: product is null");
            throw new IllegalStateException("Unsupported product type: null");
        }

        return processors.stream()
                .filter(processor -> processor.supports(product))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("No processor found for product. productId={} productType={}",
                            product.getId(),
                            product.getType()
                    );
                    return new IllegalStateException("Unsupported product type: " + product.getType());
                });
    }
}
