package com.nimbleways.springboilerplate.services.implementations;


import com.nimbleways.springboilerplate.entities.product.Product;
import com.nimbleways.springboilerplate.services.product.ProductService;
import com.nimbleways.springboilerplate.services.implementations.factory.ProcessProductStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProcessProductStrategyFactory processProductStrategyFactory;

    @Override
    public Product processProduct(Product product) {
        log.info("Process product {}", product.getName());
        return processProductStrategyFactory
                .getProductStrategy(product.getType())
                .processProduct(product);
    }
}
