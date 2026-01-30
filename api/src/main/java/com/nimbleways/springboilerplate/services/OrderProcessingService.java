package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.strategies.ProductAvailabilityStrategy;
import com.nimbleways.springboilerplate.strategies.ProductStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProcessingService {

    private final ProductStrategyFactory productStrategyFactory;


    @Transactional
    public void processOrder(Order order) {
        if (order == null || order.getItems() == null) {
            log.warn("Received null order or order with null items");
            return;
        }

        log.info("Processing order ID: {}", order.getId());

        order.getItems().forEach(this::processProduct);

        log.info("Completed processing order ID: {}", order.getId());
    }

    private void processProduct(Product product) {
        if (product == null) {
            log.warn("Encountered null product in order");
            return;
        }

        try {
            ProductType productType = parseProductType(product.getType());
            ProductAvailabilityStrategy strategy = productStrategyFactory.getStrategy(productType);
            strategy.processOrder(product);

            log.debug("Processed product: {} (type: {})", product.getName(), productType);
        } catch (IllegalArgumentException e) {
            log.error("Error processing product {}: {}", product.getName(), e.getMessage());
            throw e;
        }
    }

    private ProductType parseProductType(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Product type cannot be null");
        }

        try {
            return ProductType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid product type: " + type);
        }
    }
}
