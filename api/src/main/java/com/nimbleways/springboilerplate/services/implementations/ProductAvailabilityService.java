package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.services.implementations.availability.ProductAvailabilityStrategy;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductAvailabilityService {

    private final List<ProductAvailabilityStrategy> strategies;

    public ProductAvailabilityService(List<ProductAvailabilityStrategy> strategies) {
        this.strategies = strategies;
    }

    public void process(Product product) {
        ProductType productType = ProductType.from(product.getType());
        strategies.stream()
                .filter(strategy -> strategy.supports(productType))
                .findFirst()
                .ifPresent(strategy -> strategy.process(product));
    }
}
