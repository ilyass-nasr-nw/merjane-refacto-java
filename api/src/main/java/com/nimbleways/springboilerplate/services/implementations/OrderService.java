package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class OrderService {
    Logger logger = LogManager.getLogger(OrderService.class);

    private final ProductService ps;
    private final OrderRepository or;

    public OrderService(ProductService ps, OrderRepository or) {
        this.ps = ps;
        this.or = or;
    }

    /**
     * process order for a given id
     *
     * @param orderId target id
     * @return order id if valid, -1 otherwise
     */
    public Long processOrder(Long orderId) {
        final Optional<Order> orderOptional = or.findById(orderId);
        if (orderOptional.isEmpty()) {
            return -1L;
        }

        final Order order = orderOptional.get();
        logger.debug(order);

        final Set<Product> products = order.getItems();
        for (Product p : products) {
            if ("NORMAL".equals(p.getType())) {
                ps.processNormalProduct(p);
            } else if ("SEASONAL".equals(p.getType())) {
                ps.processSeasonalProduct(p);
            } else if ("EXPIRABLE".equals(p.getType())) {
                ps.processExpiredProduct(p);
            }
        }

        return order.getId();
    }
}