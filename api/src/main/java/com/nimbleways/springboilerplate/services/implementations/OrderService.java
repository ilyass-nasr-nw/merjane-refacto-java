package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import exception.TechnicalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final ProductService productService;

    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    public ProcessOrderResponse processOrder(Long orderId) throws TechnicalException {
        log.info("Start service processOrder with id : {}", orderId);

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new TechnicalException("Order not found"));
        List<Product> updatedProducts = new ArrayList<>();
        for (Product product : order.getItems()) {
            handleProduct(product, updatedProducts);
        }
        productRepository.saveAll(updatedProducts);
        ProcessOrderResponse processOrderResponse = new ProcessOrderResponse(order.getId());

        log.info("End service processOrder with id : {}", orderId);

        return processOrderResponse;
    }

    private void handleProduct(Product product, List<Product> updatedProducts) {
        log.info("Start service handleProduct");

        switch (product.getType()) {
            case "NORMAL":
                handleNormalProduct(product, updatedProducts);
                break;
            case "SEASONAL":
                handleSeasonalProduct(product, updatedProducts);
                break;
            case "EXPIRABLE":
                handleExpirableProduct(product, updatedProducts);
                break;
            default:
                throw new IllegalArgumentException("Unknown product type: " + product.getType());
        }
        log.info("End service handleProduct ");

    }

    private void handleNormalProduct(Product product, List<Product> updatedProducts) {
        if (product.getAvailable() > 0) {
            decrementStock(product, updatedProducts);
        } else if (product.getLeadTime() > 0) {
            productService.notifyDelay(product.getLeadTime(), product, updatedProducts);
        }
    }

    private void handleSeasonalProduct(Product product, List<Product> updatedProducts) {
        LocalDate now = LocalDate.now();
        if (now.isAfter(product.getSeasonStartDate()) && now.isBefore(product.getSeasonEndDate()) && product.getAvailable() > 0) {
            decrementStock(product, updatedProducts);
        } else {
            productService.handleSeasonalProduct(product, updatedProducts);
        }
    }

    private void handleExpirableProduct(Product product, List<Product> updatedProducts) {
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            decrementStock(product, updatedProducts);
        } else {
            productService.handleExpiredProduct(product, updatedProducts);
        }
    }

    private void decrementStock(Product product, List<Product> updatedProducts) {
        product.setAvailable(product.getAvailable() - 1);
        updatedProducts.add(product);
    }
}
