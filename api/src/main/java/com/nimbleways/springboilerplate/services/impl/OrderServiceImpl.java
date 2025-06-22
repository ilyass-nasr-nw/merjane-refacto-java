package com.nimbleways.springboilerplate.services.impl;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductTypeEnum;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.OrderService;
import com.nimbleways.springboilerplate.services.ProductService;
import com.nimbleways.springboilerplate.strategy.ProductProcessingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final Map<ProductTypeEnum, ProductProcessingStrategy> strategyMap;

    @Autowired
    public  OrderServiceImpl(OrderRepository orderRepository,
                        List<ProductProcessingStrategy> strategies) {
        this.orderRepository = orderRepository;
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(ProductProcessingStrategy::getType, s->s));
}

    @Override
    public ProcessOrderResponse processOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found : " + orderId));
        for (Product product : order.getItems()) {
            ProductTypeEnum productType = checkProductType(product);
            ProductProcessingStrategy strategy = strategyMap.get(ProductTypeEnum.NORMAL);
            if (strategy != null) {
                strategy.process(product);
            }else{
                throw new IllegalStateException("No strategy found for product type: " + productType);
            }
        }
        return new ProcessOrderResponse(order.getId());
    }


    private ProductTypeEnum checkProductType(Product product){
        if(product == null || product.getType() == null){
            throw new IllegalArgumentException("Product or product type must not be null");
        }
        return product.getType();
    }

}