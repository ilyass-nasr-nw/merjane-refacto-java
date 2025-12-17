package com.nimbleways.springboilerplate.strategy;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SeasonalProductProcessor implements ProductProcessor {

    private final ProductRepository productRepository;
    private final ProductService productService;

    @Override
    public void process(Product product) {
        if (isInSeason(product) && product.getAvailable() > 0) {
            decrementStock(product);
        } else {
            productService.handleSeasonalProduct(product);
        }
    }

    private boolean isInSeason(Product product) {
        LocalDate now = LocalDate.now();
        return now.isAfter(product.getSeasonStartDate())
                && now.isBefore(product.getSeasonEndDate());
    }

    private void decrementStock(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }
}
