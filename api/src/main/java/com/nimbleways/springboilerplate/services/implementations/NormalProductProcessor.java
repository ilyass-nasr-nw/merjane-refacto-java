package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductProcessor;
import org.springframework.stereotype.Component;

@Component
public class NormalProductProcessor implements ProductProcessor {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public NormalProductProcessor(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public boolean supports(String type) {
        return "NORMAL".equals(type);
    }

    @Override
    public void process(Product p) {
        if (p.getAvailable() > 0) {
            p.setAvailable(p.getAvailable() - 1);
            productRepository.save(p);
        } else {
            int leadTime = p.getLeadTime();
            if (leadTime > 0) {
                p.setLeadTime(leadTime); // Preserving Hamid's exact behavior
                productRepository.save(p);
                notificationService.sendDelayNotification(leadTime, p.getName());
            }
        }
    }
}