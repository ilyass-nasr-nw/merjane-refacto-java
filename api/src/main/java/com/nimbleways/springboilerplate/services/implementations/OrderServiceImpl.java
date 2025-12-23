package com.nimbleways.springboilerplate.services.implementations;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.OrderService;
import com.nimbleways.springboilerplate.services.handlers.ProductTypeHandlerFactory;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final ProductTypeHandlerFactory productTypeHandlerFactory;

    public OrderServiceImpl(OrderRepository orderRepository, ProductTypeHandlerFactory productTypeHandlerFactory) {
        this.orderRepository = orderRepository;
        this.productTypeHandlerFactory = productTypeHandlerFactory;
    }

    @Override
    public ProcessOrderResponse processOrder(Long orderId) {
        logger.info("Processing order {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        Set<Product> products = order.getItems();
        logger.debug("Order {} contains {} products", orderId, products.size());
        products.forEach(this::processOrderItem);

        logger.info("Order {} processed successfully", orderId);
        return new ProcessOrderResponse(order.getId());
    }

    private void processOrderItem(Product product) {
        logger.debug("Processing product: {} (type: {})", product.getName(), product.getType());
        productTypeHandlerFactory.getHandler(product.getType()).processProductOrder(product);
    }
}
