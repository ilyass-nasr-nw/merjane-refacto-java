package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.services.utils.Constants;

import java.time.LocalDate;

public class SeasonalProductStrategy implements ProductStrategy{
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public SeasonalProductStrategy(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public boolean supports(String productType) {
        return Constants.SEASONAL.name().equalsIgnoreCase(productType);
    }

    @Override
    public Order process(Order order, Product product) {
        LocalDate now = LocalDate.now();

        if (now.isAfter(product.getSeasonStartDate()) && now.isBefore(product.getSeasonEndDate()) && product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            Product savedProduct = productRepository.save(product);
        } else {
            handleSeasonalProduct(product, now);
        }
        return order;
    }

    private void handleSeasonalProduct(Product product, LocalDate now) {
        int leadTime = product.getLeadTime() != null ? product.getLeadTime() : 0;

        if (now.plusDays(leadTime).isAfter(product.getSeasonEndDate()) || product.getSeasonStartDate().isAfter(now)) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
            productRepository.save(product);
            throw new IllegalArgumentException("Product is out of its season : " + product.getType());

        } else {
            product.setLeadTime(leadTime);
            productRepository.save(product);
            notificationService.sendDelayNotification(leadTime, product.getName());
            throw new IllegalArgumentException("Product is out of its season : " + product.getType());
        }
    }

}
