package com.nimbleways.springboilerplate.services.strategies.product;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class ExpirableProductStrategy implements ProductTypeStrategy{

    private final NotificationService notificationService;

    private final ProductRepository productRepository;


    private boolean isProductAvailable(Product p) {
        return p.getAvailable() > 0 && p.getExpiryDate().isAfter(LocalDate.now());
    }

    private void handleAvailableProduct(Product p) {
        p.setAvailable(p.getAvailable() - 1);
        productRepository.save(p);
    }

    private void handleExpiredProduct(Product p) {
        notificationService.sendExpirationNotification(p.getName(), p.getExpiryDate());
        p.setAvailable(0);
        productRepository.save(p);
    }


    @Override
    public void process(Product p) {
        if (isProductAvailable(p)) {
            handleAvailableProduct(p);
        } else {
            handleExpiredProduct(p);
        }
    }


    @Override
    public ProductType getProductType() {
        return ProductType.EXPIRABLE;
    }
}
