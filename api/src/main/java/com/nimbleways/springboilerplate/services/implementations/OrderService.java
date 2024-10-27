package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final ProductRepository productRepository;
    private final ProductService productService;
    private final OrderRepository orderRepository;

    public void handlProductType(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Value not found"));
        log.info("************ order : " +order+ " **************");
        Set<Product> products = order.getItems();

        for (Product p : products) {
            switch (p.getType()){
                case Constants.PRODUCT_TYPE_NORMAL -> handleIfProductTypeNormal(p);
                case Constants.PRODUCT_TYPE_SEASONAL ->  handleIfProductTypeSeasonal(p);
                case Constants.PRODUCT_TYPE_EXPIRABLE ->  handleIfProductTypeExpirable(p);
                default -> throw new IllegalArgumentException("Invalid type of Product: " + p.getType());
            }
        }
    }

    private void handleIfProductTypeExpirable(Product p) {
        if (p.getAvailable() > 0 && p.getExpiryDate().isAfter(LocalDate.now())) {
            p.setAvailable(p.getAvailable() - Constants.oneToAbstruct);
            productRepository.save(p);
        } else {
            productService.handleExpiredProduct(p);
        }
    }

    private void handleIfProductTypeSeasonal(Product p) {
        if ((LocalDate.now().isAfter(p.getSeasonStartDate()) && LocalDate.now().isBefore(p.getSeasonEndDate())
                && p.getAvailable() > Constants.zero)) {
            p.setAvailable(p.getAvailable() - Constants.oneToAbstruct);
            productRepository.save(p);
        } else {
            productService.handleSeasonalProduct(p);
        }
    }

    private void handleIfProductTypeNormal(Product p) {
        if (p.getAvailable() > Constants.zero) {
            p.setAvailable(p.getAvailable() - Constants.oneToAbstruct);
            productRepository.save(p);
        } else {
            int leadTime = p.getLeadTime();
            if (leadTime > Constants.zero) {
                productService.notifyDelay(leadTime, p);
            }
        }
    }
}
