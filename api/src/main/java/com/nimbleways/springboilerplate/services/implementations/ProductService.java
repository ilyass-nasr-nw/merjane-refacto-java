package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import com.nimbleways.springboilerplate.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    NotificationService notificationService;

    public void notifyDelay(int leadTime, Product p) {
        p.setLeadTime(leadTime);
        productRepository.save(p);
        notificationService.sendDelayNotification(leadTime, p.getName());
    }

    public void handleSeasonalProduct(Product p) {

        leadTimeAfterSeasonEndDate(p);

        seasonEndDateAfterTody(p);

        notifyDelay(p);
    }

    public void handleExpiredProduct(Product p) {

        expireDateIsAfterToday(p);

        sendingExprationNotification(p);

    }

    private void seasonEndDateAfterTody(Product p) {
        if (p.getSeasonStartDate().isAfter(LocalDate.now())) {
            notificationService.sendOutOfStockNotification(p.getName());
            productRepository.save(p);
        }
    }

    private void leadTimeAfterSeasonEndDate(Product p) {
        if (LocalDate.now().plusDays(p.getLeadTime()).isAfter(p.getSeasonEndDate())) {
            notificationService.sendOutOfStockNotification(p.getName());
            p.setAvailable(Constants.zero);
            productRepository.save(p);
        }
    }

    private void notifyDelay(Product p){
        notifyDelay(p.getLeadTime(), p);
    }

    private void expireDateIsAfterToday(Product p){
        if (p.getAvailable() > Constants.zero && p.getExpiryDate().isAfter(LocalDate.now())) {
            p.setAvailable(p.getAvailable() - Constants.oneToAbstruct);
            productRepository.save(p);
        }
    }

    private void sendingExprationNotification(Product p){
        notificationService.sendExpirationNotification(p.getName(), p.getExpiryDate());
        p.setAvailable(Constants.zero);
        productRepository.save(p);
    }
}