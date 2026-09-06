package com.nimbleways.springboilerplate.services.implementations.strategies;

import com.nimbleways.springboilerplate.services.implementations.exceptions.UnsupportedProductTypeException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProductStrategyFactory {
    private final Map<String, ProductStrategy> productStrategyMap = new HashMap<>();

    public ProductStrategyFactory(List<ProductStrategy> strategies) {
        for (ProductStrategy strategy : strategies) {
            productStrategyMap.put(strategy.getProductType(), strategy);
        }
    }

    public ProductStrategy getProductStrategy(String productType) {
        ProductStrategy strategy = productStrategyMap.get(productType);
        if (strategy == null) throw new UnsupportedProductTypeException(productType);
        return strategy;
    }
}
