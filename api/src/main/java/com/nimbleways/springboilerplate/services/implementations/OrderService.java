package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final ProductService productService;
    private final OrderRepository orderRepository;

    public ProcessOrderResponse processOrder(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        System.out.println(order);
        Set<Product> products = order.getItems();
        for (Product product : products) {
            switch (product.getType()) {
                case NORMAL:
                    productService.handleNormalProduct(product);
                    break;
                case SEASONAL:
                    productService.handleSeasonalProduct(product);
                    break;
                case EXPIRABLE:
                    productService.handleExpiredProduct(product);
                    break;
                case FLASHSALE:
                    productService.handleFlashSaleProduct(product);
            }
        }

        return new ProcessOrderResponse(order.getId());
    }
}
