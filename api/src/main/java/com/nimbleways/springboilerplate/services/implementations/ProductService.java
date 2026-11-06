package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.strategies.ExpirableProductStrategy;
import com.nimbleways.springboilerplate.services.strategies.NormalProductStrategy;
import com.nimbleways.springboilerplate.services.strategies.ProductsStockStrategy;
import com.nimbleways.springboilerplate.services.strategies.SeasonalProductStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final Clock clock;
    private final Map<String, ProductsStockStrategy> strategies;

    @Autowired
    public ProductService(ProductRepository productRepository, NotificationService notificationService) {
        this(productRepository, notificationService, Clock.systemDefaultZone());
    }

    ProductService(ProductRepository productRepository, NotificationService notificationService, Clock clock) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
        this.clock = clock;
        strategies = new HashMap<>();
        strategies.put("NORMAL", new NormalProductStrategy());
        strategies.put("SEASONAL", new SeasonalProductStrategy());
        strategies.put("EXPIRABLE", new ExpirableProductStrategy());
    }

    public void notifyDelay(int leadTime, Product product) {
        product.setLeadTime(leadTime);
        productRepository.save(product);
        notificationService.sendDelayNotification(leadTime, product.getName());
    }

    public void process(Product product) {
        ProductsStockStrategy strategy = strategies.get(product.getType());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported product type: " + product.getType());
        }
        ProductsStockStrategy.Resultat resultat = strategy.evaluer(product, LocalDate.now(clock));
        if (resultat == ProductsStockStrategy.Resultat.DELAI) {
            notifyDelay(product.getLeadTime(), product);
            return;
        }
        if (resultat == ProductsStockStrategy.Resultat.DISPONIBLE) {
            product.setAvailable(product.getAvailable() - 1);
        } else if (resultat == ProductsStockStrategy.Resultat.EXPIRE) {
            product.setAvailable(0);
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
        } else {
            notificationService.sendOutOfStockNotification(product.getName());
        }
        productRepository.save(product);
    }
}