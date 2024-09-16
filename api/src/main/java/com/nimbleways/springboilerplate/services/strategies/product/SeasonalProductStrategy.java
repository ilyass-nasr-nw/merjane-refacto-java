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
public class SeasonalProductStrategy implements ProductTypeStrategy{

    private final ProductRepository productRepository;

    private final NotificationService notificationService;


    private void handleAvailableProduct(Product p) {
        p.setAvailable(p.getAvailable() - 1);
        productRepository.save(p);
    }

    private void handleOutOfSeasonProduct(Product p, LocalDate now, boolean isOutOfSeason) {
        LocalDate endOfSeason = p.getSeasonEndDate();
        boolean isPastSeason = now.plusDays(p.getLeadTime()).isAfter(endOfSeason);
        boolean isBeforeSeason = p.getSeasonStartDate().isAfter(now);

        if (isPastSeason || isBeforeSeason) {
            handleOutOfStock(p);
        } else {
            notifyDelay(p);
        }
    }

    private void handleOutOfStock(Product p) {
        notificationService.sendOutOfStockNotification(p.getName());
        p.setAvailable(0);
        productRepository.save(p);
    }

    private void notifyDelay(Product p) {
        notificationService.sendDelayNotification(p.getLeadTime(), p.getName());
    }

    @Override
    public void process(Product p) {
        LocalDate now = LocalDate.now();
        boolean isWithinSeason = now.isAfter(p.getSeasonStartDate()) && now.isBefore(p.getSeasonEndDate());
        boolean isOutOfSeason = !isWithinSeason;
        boolean isStockAvailable = p.getAvailable() > 0;

        // Process product if within season and stock is available
        if (isWithinSeason && isStockAvailable) {
            handleAvailableProduct(p);
        } else {
            handleOutOfSeasonProduct(p, now, isOutOfSeason);
        }

    }





    @Override
    public ProductType getProductType() {
        return ProductType.SEASONAL;
    }
}
