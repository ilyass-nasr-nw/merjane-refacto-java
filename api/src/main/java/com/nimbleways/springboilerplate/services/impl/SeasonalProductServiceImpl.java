package com.nimbleways.springboilerplate.services.impl;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SeasonalProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    NotificationService notificationService;


    @Override
    public void handle(Product product) {
        if (isInSeasonAndAvailable(product)) {
            handleInSeason(product);
        } else {
            handleOutOfSeason(product);
        }
    }

    private boolean isInSeasonAndAvailable(Product product) {
        return LocalDate.now().isAfter(product.getSeasonStartDate()) &&
                LocalDate.now().isBefore(product.getSeasonEndDate()) &&
                product.getAvailable() > 0;
    }

    private void handleInSeason(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    private void handleOutOfSeason(Product product) {
        if (isOutOfStockAfterLeadTime(product)) {
            sendOutOfStockAndSetZero(product);
        } else if (isSeasonNotStarted(product)) {
            sendOutOfStockAndSave(product);
        } else {
            product.setLeadTime(product.getLeadTime());
            productRepository.save(product);
            notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
        }
    }

    private boolean isOutOfStockAfterLeadTime(Product product) {
        return LocalDate.now().plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate());
    }

    private void sendOutOfStockAndSetZero(Product product) {
        notificationService.sendOutOfStockNotification(product.getName());
        product.setAvailable(0);
        productRepository.save(product);
    }

    private boolean isSeasonNotStarted(Product product) {
        return product.getSeasonStartDate().isAfter(LocalDate.now());
    }

    private void sendOutOfStockAndSave(Product product) {
        notificationService.sendOutOfStockNotification(product.getName());
        productRepository.save(product);
    }


}
