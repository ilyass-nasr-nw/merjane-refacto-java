package com.nimbleways.springboilerplate.services.product;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.services.product.factory.ProductFactory;
import com.nimbleways.springboilerplate.services.product.strategy.HandleProductStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductFactory productStrategyFactory;

    public void processProduct(Product product) {
        HandleProductStrategy strategy = productStrategyFactory.getStrategy(product);
        strategy.handleProduct(product);
    }
}