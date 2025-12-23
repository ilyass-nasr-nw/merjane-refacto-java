package com.nimbleways.springboilerplate.handler;


import com.nimbleways.springboilerplate.constants.ProductType;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NormalProduct implements ProductHandler {
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public boolean supports(ProductType type) {
        return type == ProductType.NORMAL;
    }

    @Override
    public void manage(Product product) {
        if (product.getAvailable() > 0) {
            updateStock(product);
        } else {
        	soldOut(product);
        }
    }

    private void updateStock(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    private void soldOut(Product product) {
        if (product.getLeadTime() > 0) {
            notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
            productRepository.save(product);
        }
    }
}