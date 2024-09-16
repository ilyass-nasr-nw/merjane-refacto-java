package com.nimbleways.springboilerplate.services.strategies.product;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NormalProductStrategy implements ProductTypeStrategy{


    private final ProductRepository productRepository;

    private final NotificationService notificationService;


    private boolean isAvailable(Product p) {
        return p.getAvailable() > 0;
    }

    private void handleAvailableProduct(Product p) {
        p.setAvailable(p.getAvailable() - 1);
        productRepository.save(p);
    }

    private void handleUnavailableProduct(Product p) {
        int leadTime = p.getLeadTime();
        if (leadTime > 0) {
            notificationService.sendDelayNotification(leadTime, p.getName());
        }
    }

    @Override
    public void process(Product p) {

        if (isAvailable(p)) {
            handleAvailableProduct(p);
        } else {
            handleUnavailableProduct(p);
        }

    }

    @Override
    public ProductType getProductType() {
        return ProductType.NORMAL;
    }
}
