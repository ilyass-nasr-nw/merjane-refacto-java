package com.nimbleways.springboilerplate.services.implementations.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NormalProductStrategy implements ProductStrategy {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;


    @Override
    public String getProductType() {
        return "NORMAL";
    }

    @Override
    public void handleOrder(Product product) {
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
            return;
        }

        if (product.getLeadTime() > 0) {
            notificationService.sendDelayNotification(
                    product.getLeadTime(),
                    product.getName()
            );
        }
    }
}
