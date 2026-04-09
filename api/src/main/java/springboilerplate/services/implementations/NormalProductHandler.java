package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductHandler;
import org.springframework.stereotype.Service;

@Service
public class NormalProductHandler implements ProductHandler {
    private final ProductRepository productRepository;
    private final NotificationServiceImpl notificationService;

    public NormalProductHandler(ProductRepository productRepository,
                                NotificationServiceImpl notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void handle(Product product) {
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        }
        int leadTime = product.getLeadTime();
        if (leadTime > 0) {
            notificationService.sendDelayNotification(leadTime, product.getName());
        }
    }

}
