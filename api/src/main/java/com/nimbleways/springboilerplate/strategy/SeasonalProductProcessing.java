package com.nimbleways.springboilerplate.strategy;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductTypeEnum;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SeasonalProductProcessing implements ProductProcessingStrategy{

    private final ProductService productService;
    private final ProductRepository productRepository;


    @Override
    public ProductTypeEnum getType() {
        return ProductTypeEnum.SEASONAL;
    }

    @Override
    public void process(Product product) {
        if (isInSeasonAndInStock(product)) {
            decreaseStock(product);
        } else {
            productService.handleSeasonalProduct(product);
        }
    }

    private boolean isInSeasonAndInStock(Product product) {
        LocalDate now = LocalDate.now();
        return now.isAfter(product.getSeasonStartDate()) &&
                now.isBefore(product.getSeasonEndDate()) &&
                product.getAvailable() > 0;
    }

    private void decreaseStock(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }
}