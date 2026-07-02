package com.nimbleways.springboilerplate.services.processing;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;

@Component
public class ExpirableProductProcessingStrategy implements ProductProcessingStrategy {

    private static final String PRODUCT_TYPE = "EXPIRABLE";

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public ExpirableProductProcessingStrategy(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    /**
     * Checks if the strategy supports the given product type.
     * @param product The product to check.
     * @return true if the strategy supports the product type
    */
    @Override
    public boolean supports(Product product) {
        return PRODUCT_TYPE.equals(product.getType());
    }

    /**
     * Processes the givven produvt based on its availability and expiration date.
     * If the product is available and not expired, it decrements the availability.
     * If the product is expired, it sends an expiration notification and sets availability to 0.
     * @param product The product to process.
     */
    @Override
    public void process(Product product) {
        // Check if the product is available and not expired
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
            return;
        }
        // If the product is expired, send an expiration notification and set availability to 0
        notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
        product.setAvailable(0);
        productRepository.save(product);
    }
}
