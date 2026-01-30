package com.nimbleways.springboilerplate.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ExpirableProductStrategy implements ProductAvailabilityStrategy{
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public void processOrder(Product product) {
        if (isAvailableAndNotExpired(product)) {
            decrementAvailability(product);
        } else {
            handleExpiredProduct(product);
        }
    }

    private boolean isAvailableAndNotExpired(Product product) {
        return product.getAvailable() != null
                && product.getAvailable() > 0
                && product.getExpiryDate() != null
                && product.getExpiryDate().isAfter(LocalDate.now());
    }

    private void decrementAvailability(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    private void handleExpiredProduct(Product product) {
        LocalDate expiryDate = product.getExpiryDate() != null
                ? product.getExpiryDate()
                : LocalDate.now();

        notificationService.sendExpirationNotification(product.getName(), expiryDate);
        product.setAvailable(0);
        productRepository.save(product);
    }
}
