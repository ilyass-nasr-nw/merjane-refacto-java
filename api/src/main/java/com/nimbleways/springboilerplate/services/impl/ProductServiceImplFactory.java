package com.nimbleways.springboilerplate.services.impl;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.services.ProductService;
import com.nimbleways.springboilerplate.services.ProductServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class ProductServiceImplFactory implements ProductServiceFactory {


    @Autowired
    @Qualifier("expiredProductServiceImpl")
    private ExpiredProductServiceImpl expiredProductServiceImpl;

    @Autowired
    @Qualifier("flashSaleProductServiceImpl")
    private FlashSaleProductServiceImpl flashSaleProductServiceImpl;

    @Autowired
    @Qualifier("normalProductServiceImpl")
    private NormalProductServiceImpl normalProductServiceImpl;

    @Autowired
    @Qualifier("seasonalProductServiceImpl")
    private SeasonalProductServiceImpl seasonalProductServiceImpl;


    @Override
    public void handleProduct(Product product) {
            getProductProcessService(product.getType()).handle(product);
    }

    private ProductService getProductProcessService(String type) {
        return switch (type) {
            case "EXPIRABLE" -> expiredProductServiceImpl;
            case "SEASONAL" -> seasonalProductServiceImpl;
            case "FLASHSALE" -> flashSaleProductServiceImpl;
            default -> normalProductServiceImpl;
        };
    }
}
