package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.enums.ProductType;
import com.nimbleways.springboilerplate.exceptions.ProductTypeNotFoundException;
import com.nimbleways.springboilerplate.services.ProductService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {

    private final NormalProductHandler normalProductHandler;
    private final SeasonalProductHandler seasonalProductHandler;
    private final ExpiredProductHandler expiredProductHandler;

    public ProductServiceImpl(NormalProductHandler normalProductHandler,
                              SeasonalProductHandler seasonalProductHandler,
                              ExpiredProductHandler expiredProductHandler) {
        this.normalProductHandler = normalProductHandler;
        this.seasonalProductHandler = seasonalProductHandler;
        this.expiredProductHandler = expiredProductHandler;
    }

    @Override
    public void process(Set<Product> products) {
        for (Product product : products) {
            final ProductType type = product.getType();
            switch (type) {
                case NORMAL -> this.normalProductHandler.handle(product);
                case SEASONAL -> this.seasonalProductHandler.handle(product);
                case EXPIRABLE -> this.expiredProductHandler.handle(product);
                default -> throw new ProductTypeNotFoundException("Unknown product type: " + type);
            }
        }
    }
}
