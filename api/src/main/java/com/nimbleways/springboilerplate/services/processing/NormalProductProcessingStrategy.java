package com.nimbleways.springboilerplate.services.processing;

import org.springframework.stereotype.Component;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;

@Component
public class NormalProductProcessingStrategy implements ProductProcessingStrategy {

    private static final String PRODUCT_TYPE = "NORMAL";

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public NormalProductProcessingStrategy(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public boolean supports(Product product) {
        return PRODUCT_TYPE.equals(product.getType());
    }

    @Override
    public void process(Product product) {
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
            return;
        }

        int leadTime = product.getLeadTime();
        if (leadTime > 0) {
            product.setLeadTime(leadTime);
            productRepository.save(product);
            notificationService.sendDelayNotification(leadTime, product.getName());
        }
    }
}
