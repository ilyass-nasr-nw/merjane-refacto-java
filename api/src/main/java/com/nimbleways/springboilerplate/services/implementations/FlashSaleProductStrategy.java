package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.FlashSaleProduct;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.services.ProductStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class FlashSaleProductStrategy implements ProductStrategy {



    @Override
    public Product processOrder(Product product) {
        FlashSaleProduct flashSaleProduct = (FlashSaleProduct) product;
        if (flashSaleProduct.getAvailable() > 0 && LocalDate.now().isBefore(flashSaleProduct.getEndDate())) {
            flashSaleProduct.setAvailable(flashSaleProduct.getAvailable() - 1);
            return flashSaleProduct;
        } else {
            throw new IllegalStateException("Flash sale product is no longer available.");
        }
    }
}
