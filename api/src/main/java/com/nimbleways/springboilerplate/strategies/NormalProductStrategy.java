package com.nimbleways.springboilerplate.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NormalProductStrategy implements ProductAvailabilityStrategy {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public void processOrder(Product product) {
        if (isProductAvailable(product)) {
            decrementAvailability(product);
        } else {
            handleOutOfStock(product);
        }
    }

    private boolean isProductAvailable(Product product) {
        return product.getAvailable() != null && product.getAvailable() > 0;
    }

    private void decrementAvailability(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    private void handleOutOfStock(Product product) {
        int leadTime = product.getLeadTime() != null ? product.getLeadTime() : 0;
        if (leadTime > 0) {
            notifyCustomerAboutDelay(product, leadTime);
        }
    }

    private void notifyCustomerAboutDelay(Product product, int leadTime) {
        product.setLeadTime(leadTime);
        productRepository.save(product);
        notificationService.sendDelayNotification(leadTime, product.getName());
    }
}
