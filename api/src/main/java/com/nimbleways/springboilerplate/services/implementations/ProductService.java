package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import com.nimbleways.springboilerplate.entities.ProductType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NotificationService notificationService;

    public void processProduct(Product product) {
        switch (ProductType.valueOf(product.getType())) {
            case NORMAL:
                handleNormalProduct(product);
                break;
            case SEASONAL:
                handleSeasonalProduct(product);
                break;
            case EXPIRABLE:
                handleExpirableProduct(product);
                break;
            default:
                throw new UnsupportedOperationException("Unknown product type");
        }
    }

    private void handleNormalProduct(Product product) {
        if (product.getAvailable() > 0) {
            decrementProductAvailability(product);
        } else {
            notifyDelay(product.getLeadTime(), product);
        }
    }

    private void handleSeasonalProduct(Product product) {
        if (isWithinSeason(product) && product.getAvailable() > 0) {
            decrementProductAvailability(product);
        } else if (product.getLeadTime() > 0 && !isPastSeason(product)) {
            notifyDelay(product.getLeadTime(), product);
        } else {
            notificationService.sendOutOfStockNotification(product.getName());
        }
    }

    private void handleExpirableProduct(Product product) {
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            decrementProductAvailability(product);
        } else {
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            product.setAvailable(0);
            productRepository.save(product);
        }
    }

    private boolean isWithinSeason(Product product) {
        LocalDate now = LocalDate.now();
        return now.isAfter(product.getSeasonStartDate()) && now.isBefore(product.getSeasonEndDate());
    }

    private boolean isPastSeason(Product product) {
        return LocalDate.now().isAfter(product.getSeasonEndDate());
    }

    private void decrementProductAvailability(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    public void notifyDelay(int leadTime, Product product) {
        product.setLeadTime(leadTime);
        productRepository.save(product);
        notificationService.sendDelayNotification(leadTime, product.getName());
    }
}
