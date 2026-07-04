package com.nimbleways.merjane.services.handlers;

import com.nimbleways.merjane.persistence.entities.Product;
import com.nimbleways.merjane.domain.ProductType;
import com.nimbleways.merjane.persistence.repositories.ProductRepository;
import com.nimbleways.merjane.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SeasonalProductHandler implements ProductTypeHandler {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public ProductType getSupportedType() {
        return ProductType.SEASONAL;
    }

    @Override
    public void handle(Product product) {
        LocalDate now = LocalDate.now();
        if (now.isAfter(product.getSeasonStartDate()) && now.isBefore(product.getSeasonEndDate()) && product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else if (now.plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
            productRepository.save(product);
        } else if (product.getSeasonStartDate().isAfter(now)) {
            notificationService.sendOutOfStockNotification(product.getName());
            productRepository.save(product);
        } else {
            productRepository.save(product);
            notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
        }
    }
}
