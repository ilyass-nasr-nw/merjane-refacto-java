package com.nimbleways.springboilerplate.services.product.factory;

import com.nimbleways.springboilerplate.entities.ExpirableProduct;
import com.nimbleways.springboilerplate.entities.NormalProduct;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.SeasonalProduct;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.notification.NotificationService;
import com.nimbleways.springboilerplate.services.product.strategy.HandleExpirableProductStrategy;
import com.nimbleways.springboilerplate.services.product.strategy.HandleNormalProductStrategy;
import com.nimbleways.springboilerplate.services.product.strategy.HandleProductStrategy;
import com.nimbleways.springboilerplate.services.product.strategy.HandleSeasonalProductStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFactory {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public HandleProductStrategy getStrategy(Product product) {
        switch (product.getType()) {
            case NORMAL:
                if (product instanceof NormalProduct) {
                    return new HandleNormalProductStrategy(productRepository, notificationService);
                }
                break;
            case SEASONAL:
                if (product instanceof SeasonalProduct) {
                    return new HandleSeasonalProductStrategy(productRepository, notificationService);
                }
                break;
            case EXPIRABLE:
                if (product instanceof ExpirableProduct) {
                    return new HandleExpirableProductStrategy(productRepository, notificationService);
                }
                break;
        }

        throw new IllegalArgumentException("Unexpected product type: " +
                (product != null ? product.getClass().getName() : "null"));
    }
}