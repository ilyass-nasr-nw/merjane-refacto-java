package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.exceptions.FunctionalException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final ProductService productService;
    private final OrderRepository orderRepository;

    public ProcessOrderResponse processOrder(Long orderId) throws FunctionalException {
        log.info("Start service processOrder with id : {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new FunctionalException("Order not found"));
        List<Product> productsToSave = new ArrayList<>();
        order.getItems().forEach(item -> handleProductByType(item, productsToSave));

        productService.updateProducts(productsToSave);
        ProcessOrderResponse processOrderResponse = new ProcessOrderResponse(order.getId());

        log.info("End service processOrder with id : {}", orderId);
        return processOrderResponse;
    }

    void handleProductByType(Product item, List<Product> productsToSave) {
        switch (item.getType()) {
            case NORMAL:
                productService.handleNormalProduct(item, productsToSave);
                break;
            case SEASONAL:
                productService.handleSeasonalProduct(item, productsToSave);
                break;
            case EXPIRABLE:
                productService.handleExpiredProduct(item, productsToSave);
                break;
            default:
                throw new IllegalArgumentException("Invalid product type");
        }
    }

}
