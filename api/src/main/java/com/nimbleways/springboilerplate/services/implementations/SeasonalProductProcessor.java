package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductHelper;
import com.nimbleways.springboilerplate.services.ProductProcessor;

@Service("SEASONAL")
public class SeasonalProductProcessor implements ProductProcessor {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    NotificationService notificationService;

    @Override
    public void processProduct(Product product) {
        if ((LocalDate.now().isAfter(product.getSeasonStartDate())
                && LocalDate.now().isBefore(product.getSeasonEndDate())
                && product.getAvailable() > 0)) {
            ProductHelper.decreaseStock(product);
        } else {
            handleSeasonalProduct(product);
        }
    }

    public void handleSeasonalProduct(Product p) {
        if (LocalDate.now().plusDays(p.getLeadTime()).isAfter(p.getSeasonEndDate())) {
            notificationService.sendOutOfStockNotification(p.getName());
            ProductHelper.setOutOfStock(p);
        } else if (p.getSeasonStartDate().isAfter(LocalDate.now())) {
            notificationService.sendOutOfStockNotification(p.getName());
            productRepository.save(p);
        } else {
            ProductHelper.notifyDelay(p.getLeadTime(), p);
        }
    }
}