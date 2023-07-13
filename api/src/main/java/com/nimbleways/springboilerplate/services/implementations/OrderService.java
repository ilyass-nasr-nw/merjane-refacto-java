package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    public ProcessOrderResponse processOrder(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        System.out.println(order);
        List<Long> ids = new ArrayList<>();
        ids.add(orderId);
        Set<Product> products = order.getItems();
        for (Product p : products) {
            if (p.getType().equals("NORMAL")) {
                if (p.getAvailable() > 0) {
                    p.setAvailable(p.getAvailable() - 1);
                    productRepository.save(p);
                } else {
                    int leadTime = p.getLeadTime();
                    if (leadTime > 0) {
                        productService.notifyDelay(leadTime, p);
                    }
                }
            } else if (p.getType().equals("SEASONAL")) {
                // Add new season rules
                if ((LocalDate.now().isAfter(p.getSeasonStartDate()) && LocalDate.now().isBefore(p.getSeasonEndDate())
                        && p.getAvailable() > 0)) {
                    p.setAvailable(p.getAvailable() - 1);
                    productRepository.save(p);
                } else {
                    productService.handleSeasonalProduct(p);
                }
            } else if (p.getType().equals("EXPIRABLE")) {
                if (p.getAvailable() > 0 && p.getExpiryDate().isAfter(LocalDate.now())) {
                    p.setAvailable(p.getAvailable() - 1);
                    productRepository.save(p);
                } else {
                    productService.handleExpiredProduct(p);
                }
            } else if (p.getType().equals("FLASHSALE")) {
                productService.handleFlashSaleProduct(p);
            }
        }

        return new ProcessOrderResponse(order.getId());
    }
}
