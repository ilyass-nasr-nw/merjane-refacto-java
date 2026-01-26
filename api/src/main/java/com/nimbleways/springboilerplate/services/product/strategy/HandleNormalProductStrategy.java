package com.nimbleways.springboilerplate.services.product.strategy;

import com.nimbleways.springboilerplate.entities.NormalProduct;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.notification.NotificationService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HandleNormalProductStrategy implements HandleProductStrategy {
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public void handleProduct(Product p) {
        NormalProduct normalProduct = (NormalProduct) p;
        if (normalProduct.getAvailable() > 0) {
            normalProduct.setAvailable(p.getAvailable() - 1);
            productRepository.save(normalProduct);
        } else {
            int leadTime = p.getLeadTime();
            if (leadTime > 0) {
                notifyDelay(leadTime, normalProduct, productRepository, notificationService);
            }
        }
    }
}