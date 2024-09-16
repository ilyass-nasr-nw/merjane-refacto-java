package com.nimbleways.springboilerplate.services;

import java.util.List;
import java.util.Optional;

import com.nimbleways.springboilerplate.services.strategies.product.ProductTypeStrategy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;


/**
 * Service class for handling product operations, including processing products
 * based on their type-specific strategies.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ProductService {

    private final List<ProductTypeStrategy> strategies;

    /**
     * Processes a single product based on its type-specific strategy.
     *
     * @param product The product to be processed.
     * @throws IllegalArgumentException if no strategy is found for the product type.
     */
    public void processProduct(Product product) {
        ProductTypeStrategy strategy = findStrategyForProduct(product)
                .orElseThrow(() -> new IllegalArgumentException("No strategy found for product type: " + product.getType()));

        log.info("@ProductService : Start processing product {} by strategy {}", product.getId(), strategy.getProductType().name());
        strategy.process(product);
        log.info("@ProductService : Processing finished for product {} by strategy {}", product.getId(), strategy.getProductType().name());
    }

    /**
     * Finds the strategy associated with the product's type.
     *
     * @param product The product for which to find the strategy.
     * @return An Optional containing the strategy if found, otherwise an empty Optional.
     */
    private Optional<ProductTypeStrategy> findStrategyForProduct(Product product) {
        return strategies.stream()
                .filter(s -> s.getProductType().equals(product.getType()))
                .findFirst();
    }


}