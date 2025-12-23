package com.nimbleways.springboilerplate.handler;

import com.nimbleways.springboilerplate.constants.ProductType;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ExpiredProduct implements ProductHandler {
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public boolean supports(ProductType type) {
        return type == ProductType.EXPIRABLE;
    }

    @Override
    public void manage(Product product) {
        LocalDate today = LocalDate.now();
        boolean isAvailable = product.getAvailable() > 0;
        boolean notExpired = product.getExpiryDate().isAfter(today);

        if (isAvailable && notExpired) {
            decrementStock(product);
        } else {
            notifyExpiration(product);
        }

        productRepository.save(product);
    }

    private void decrementStock(Product product) {
        product.setAvailable(product.getAvailable() - 1);
    }

    private void notifyExpiration(Product product) {
        notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
        product.setAvailable(0);
    }
}
