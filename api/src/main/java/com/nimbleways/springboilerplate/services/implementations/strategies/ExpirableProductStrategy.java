package com.nimbleways.springboilerplate.services.implementations.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ExpirableProductStrategy implements ProductStrategy {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public String getProductType() {
        return "EXPIRABLE";
    }

    @Override
    public void handleOrder(Product product) {
        LocalDate today = LocalDate.now();

        boolean isExpired = product.getExpiryDate().isBefore(today);

        if (isExpired) {
            product.setAvailable(0);
            productRepository.save(product);

            notificationService.sendExpirationNotification(
                    product.getName(),
                    product.getExpiryDate()
            );
            return;
        }

        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        }
    }
}