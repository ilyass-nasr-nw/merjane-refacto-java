package com.nimbleways.merjane.services.handlers;

import com.nimbleways.merjane.persistence.entities.Product;
import com.nimbleways.merjane.domain.ProductType;
import com.nimbleways.merjane.persistence.repositories.ProductRepository;
import com.nimbleways.merjane.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ExpirableProductHandler implements ProductTypeHandler {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public ProductType getSupportedType() {
        return ProductType.EXPIRABLE;
    }

    @Override
    public void handle(Product product) {
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            product.setAvailable(0);
            productRepository.save(product);
        }
    }
}
