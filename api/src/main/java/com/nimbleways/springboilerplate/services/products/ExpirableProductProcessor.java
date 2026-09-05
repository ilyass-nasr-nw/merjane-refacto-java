package com.nimbleways.springboilerplate.services.products;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;

import java.time.Clock;
import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpirableProductProcessor implements ProductProcessor {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final Clock clock;

    @Override
    public ProductType supportedType() {
        return ProductType.EXPIRABLE;
    }

    @Override
    public void process(Product product) {
        if (product.isInStock() && !product.hasExpiredOn(LocalDate.now(clock))) {
            product.decrementStock();
            productRepository.save(product);
        } else {
            // Running out of stock is treated exactly like expiring: the remaining
            // units are written off and the customer gets an expiration notice.
            product.markOutOfStock();
            productRepository.save(product);
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
        }
    }
}
