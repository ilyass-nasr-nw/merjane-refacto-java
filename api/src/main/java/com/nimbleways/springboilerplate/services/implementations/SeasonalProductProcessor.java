package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductProcessor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class SeasonalProductProcessor implements ProductProcessor {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public SeasonalProductProcessor(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public boolean supports(String type) {
        return "SEASONAL".equals(type);
    }

    @Override
    public void process(Product p) {
        LocalDate now = LocalDate.now();
        if (now.isAfter(p.getSeasonStartDate()) && now.isBefore(p.getSeasonEndDate()) && p.getAvailable() > 0) {
            p.setAvailable(p.getAvailable() - 1);
            productRepository.save(p);
        } else {
            if (now.plusDays(p.getLeadTime()).isAfter(p.getSeasonEndDate())) {
                notificationService.sendOutOfStockNotification(p.getName());
                p.setAvailable(0);
                productRepository.save(p);
            } else if (p.getSeasonStartDate().isAfter(now)) {
                notificationService.sendOutOfStockNotification(p.getName());
                productRepository.save(p);
            } else {
                p.setLeadTime(p.getLeadTime());
                productRepository.save(p);
                notificationService.sendDelayNotification(p.getLeadTime(), p.getName());
            }
        }
    }
}