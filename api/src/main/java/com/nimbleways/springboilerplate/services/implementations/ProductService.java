package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    ProductRepository pr;

    @Autowired
    NotificationService ns;

    public void notifyDelay(int leadTime, Product p) {
        p.setLeadTime(leadTime);
        pr.save(p);
        ns.sendDelayNotification(leadTime, p.getName());
    }

    public void processProduct(Product p) {
        switch (p.getType()) {
            case NORMAL:
                handleNormalProduct(p);
                break;
            case SEASONAL:
                handleSeasonalProduct(p);
                break;
            case EXPIRABLE:
                handleExpirableProduct(p);
                break;
            case FLASHSALE:
                handleFlashSaleProduct(p);
                break;
        }
    }

    private void handleNormalProduct(Product p) {
        if (p.getAvailable() > 0) {
            p.setAvailable(p.getAvailable() - 1);
            pr.save(p);
        } else if (p.getLeadTime() > 0) {
            notifyDelay(p.getLeadTime(), p);
        }
    }

    private void handleSeasonalProduct(Product p) {
        LocalDate now = LocalDate.now();
        if (now.isAfter(p.getSeasonStartDate()) && now.isBefore(p.getSeasonEndDate()) && p.getAvailable() > 0) {
            p.setAvailable(p.getAvailable() - 1);
            pr.save(p);
        } else {
            if (now.plusDays(p.getLeadTime()).isAfter(p.getSeasonEndDate())) {
                ns.sendOutOfStockNotification(p.getName());
                p.setAvailable(0);
                pr.save(p);
            } else if (p.getSeasonStartDate().isAfter(LocalDate.now())) {
                ns.sendOutOfStockNotification(p.getName());
                pr.save(p);
            } else {
                notifyDelay(p.getLeadTime(), p);
            }
        }
    }

    private void handleExpirableProduct(Product p) {
        if (p.getAvailable() > 0 && p.getExpiryDate().isAfter(LocalDate.now())) {
            p.setAvailable(p.getAvailable() - 1);
        } else if (p.getExpiryDate().isAfter(LocalDate.now())){
            ns.sendExpirationNotification(p.getName(), p.getExpiryDate());
            p.setAvailable(0);
        } else {
            ns.sendOutOfStockNotification(p.getName());
        }
        pr.save(p);
    }

    private void handleFlashSaleProduct(Product p) {
        LocalDate now = LocalDate.now();
        if (now.isAfter(p.getFlashSaleStartDate()) && now.isBefore(p.getFlashSaleEndDate()) && p.getAvailable() > 0 && p.getMaxQuantity() > 0) {
            p.setAvailable(p.getAvailable() - 1);
            p.setMaxQuantity(p.getMaxQuantity() - 1);
        } else if (now.isAfter(p.getFlashSaleEndDate())) {
            ns.sendExpirationNotification(p.getName(), p.getFlashSaleEndDate());
            p.setAvailable(0);
        } else if (p.getMaxQuantity() == 0) {
            ns.sendOutOfStockNotification(p.getName());
            p.setAvailable(0);
        }
        pr.save(p);
    }
}