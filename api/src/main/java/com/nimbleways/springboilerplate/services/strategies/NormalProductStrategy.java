package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.services.utils.Constants;

public class NormalProductStrategy implements ProductStrategy{

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public NormalProductStrategy(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public boolean supports(String productType) {
        return Constants.NORMAL.name().equalsIgnoreCase(productType);
    }

    @Override
    public Order process(Order order, Product product) {
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            int leadTime = product.getLeadTime() != null ? product.getLeadTime() : 0;
            if (leadTime > 0) {
                product.setLeadTime(leadTime);
                productRepository.save(product);
                notificationService.sendDelayNotification(leadTime, product.getName());
                throw new IllegalArgumentException("Product is not in stock: " + product.getType());
            }
        }
        return order;
    }


}
