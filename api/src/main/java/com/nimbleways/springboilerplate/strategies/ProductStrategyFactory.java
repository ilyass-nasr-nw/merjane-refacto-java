package com.nimbleways.springboilerplate.strategies;

import com.nimbleways.springboilerplate.entities.ProductType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductStrategyFactory {

    private final NormalProductStrategy normalProductStrategy;
    private final SeasonalProductStrategy seasonalProductStrategy;
    private final ExpirableProductStrategy expirableProductStrategy;


    public ProductAvailabilityStrategy getStrategy(ProductType productType) {
        Map<ProductType, ProductAvailabilityStrategy> strategies = Map.of(
                ProductType.NORMAL, normalProductStrategy,
                ProductType.SEASONAL, seasonalProductStrategy,
                ProductType.EXPIRABLE, expirableProductStrategy
        );

        ProductAvailabilityStrategy strategy = strategies.get(productType);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown product type: " + productType);
        }

        return strategy;
    }
}
