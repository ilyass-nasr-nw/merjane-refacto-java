package com.nimbleways.springboilerplate.strategy;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductTypeEnum;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NormalProductProcessing implements ProductProcessingStrategy {

    private final ProductService productService;
    private final ProductRepository productRepository;

    @Override
    public ProductTypeEnum getType() {
        return ProductTypeEnum.NORMAL;
    }


    /*
     * Taking into consideration that every attribute is not null and required in the creation service in the DTO
     * */

    @Override
    public void process(Product product) {
        if (isInStock(product)) {
            decreaseStock(product);
        } else {
            handleOutOfStock(product);
        }
    }

    private boolean isInStock(Product product) {
        return product.getAvailable() > 0;
    }

    private void decreaseStock(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    private void handleOutOfStock(Product product) {
        int leadTime = product.getLeadTime();
        if (leadTime > 0) {
            productService.notifyDelay(leadTime, product);
        }
    }

}