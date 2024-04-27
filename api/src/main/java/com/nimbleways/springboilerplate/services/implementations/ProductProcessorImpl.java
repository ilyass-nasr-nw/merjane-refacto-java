package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductProcessor;
import com.nimbleways.springboilerplate.services.ProductStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

@Service
public class ProductProcessorImpl implements ProductProcessor {
    private final Map<ProductType, ProductStrategy> strategies;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;




    public ProductProcessorImpl(
            @Autowired NormalProductStrategy normalProductStrategy,
            @Autowired SeasonalProductStrategy seasonalProductStrategy,
            @Autowired ExpirableProductStrategy expirableProductStrategy,
            @Autowired FlashSaleProductStrategy flashSaleProductStrategy
    ) {
        strategies = new EnumMap<>(ProductType.class);
        strategies.put(ProductType.NORMAL, normalProductStrategy);
        strategies.put(ProductType.SEASONAL, seasonalProductStrategy);
        strategies.put(ProductType.EXPIRABLE, expirableProductStrategy);
        strategies.put(ProductType.FLASHSALE, flashSaleProductStrategy);
    }

    public Product processOrder(Product product) {
        ProductType productType = ProductType.valueOf(product.getType());
        ProductStrategy strategy = strategies.get(productType);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for product type: " + productType);
        }
        return strategy.processOrder(product);
    }
}
