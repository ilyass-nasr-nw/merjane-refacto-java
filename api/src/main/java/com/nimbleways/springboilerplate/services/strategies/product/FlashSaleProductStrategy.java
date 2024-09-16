package com.nimbleways.springboilerplate.services.strategies.product;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class FlashSaleProductStrategy implements ProductTypeStrategy{


    private final ProductRepository productRepository;


    @Override
    public void process(Product p) {
        LocalDate today = LocalDate.now();

        boolean isFlashSaleActive = today.isAfter(p.getFlashSaleStartDate()) && today.isBefore(p.getFlashSaleEndDate());

        if (p.getAvailable() > 0 && isFlashSaleActive) {
            p.setAvailable(p.getAvailable() - 1);
        } else {
            p.setAvailable(0);
        }

        productRepository.save(p);
    }


    @Override
    public ProductType getProductType() {
        return ProductType.FLASHSALE;
    }
}
