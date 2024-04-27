package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;


    /**
     * @param orderId Order Id
     * Process the order and update the product availability
     * @return Order Id
     */
    @Override
    public Long processOrder(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (!order.isPresent()) {
            throw new RuntimeException("Order not found:" + orderId);
        }

        System.out.println(order);
        List<Long> ids = new ArrayList<>();
        ids.add(orderId);
        Set<Product> products = order.get().getItems();
        for (Product p : products) {
            if (p.getType().equals("NORMAL")) {
                processOrderNorma(p);
            } else if (p.getType().equals("SEASONAL")) {
                processOrderSeasonal(p);
            } else if (p.getType().equals("EXPIRABLE")) {
                processOrderExpirable(p);
            } else if (p.getType().equals("FLASH_SALE")) {
                processOrderFlashSale(p);
            }
        }


        return order.get().getId();
    }

    private void processOrderFlashSale(Product p) {
        throw new RuntimeException("Method not implemented yet");
    }

    private void processOrderExpirable(Product p) {
        if (p.getAvailable() > 0 && p.getExpiryDate().isAfter(LocalDate.now())) {
            p.setAvailable(p.getAvailable() - 1);
            productRepository.save(p);
        } else {
            productService.handleExpiredProduct(p);
        }
    }

    private void processOrderSeasonal(Product p) {
        // Add new season rules
        if ((LocalDate.now().isAfter(p.getSeasonStartDate()) && LocalDate.now().isBefore(p.getSeasonEndDate())
                && p.getAvailable() > 0)) {
            p.setAvailable(p.getAvailable() - 1);
            productRepository.save(p);
        } else {
            productService.handleSeasonalProduct(p);
        }
    }

    private void processOrderNorma(Product p) {
        if (p.getAvailable() > 0) {
            p.setAvailable(p.getAvailable() - 1);
            productRepository.save(p);
        } else {
            int leadTime = p.getLeadTime();
            if (leadTime > 0) {
                productService.notifyDelay(leadTime, p);
            }
        }
    }
}
