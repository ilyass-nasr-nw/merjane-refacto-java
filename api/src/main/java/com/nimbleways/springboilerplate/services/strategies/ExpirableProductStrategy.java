package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.services.utils.Constants;

import java.time.LocalDate;

public class ExpirableProductStrategy implements ProductStrategy {
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public ExpirableProductStrategy(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public boolean supports(String productType) {
        return Constants.EXPIRABLE.name().equalsIgnoreCase(productType);
    }

    @Override
    public Order process(Order order, Product product) {
        LocalDate now = LocalDate.now();

        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(now)) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            product.setAvailable(0);
            productRepository.save(product);

        }

        return order;
    }
}
