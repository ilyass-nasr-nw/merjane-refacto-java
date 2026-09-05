package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.products.ProductProcessor;
import com.nimbleways.springboilerplate.services.products.ProductProcessorResolver;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@ExtendWith(MockitoExtension.class)
class OrderProcessingServiceTests {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductProcessorResolver processorResolver;
    @Mock
    private ProductProcessor processor;

    private OrderProcessingService service;

    @BeforeEach
    void setUp() {
        service = new OrderProcessingService(orderRepository, processorResolver);
    }

    @Test
    void processesEveryItemOfTheOrder() {
        Product cable = product(ProductType.NORMAL, "USB Cable");
        Product milk = product(ProductType.EXPIRABLE, "Milk");
        Order order = order(42L, cable, milk);
        when(orderRepository.findById(42L)).thenReturn(Optional.of(order));
        when(processorResolver.resolve(any())).thenReturn(processor);

        Order processed = service.processOrder(42L);

        assertThat(processed.getId()).isEqualTo(42L);
        verify(processorResolver).resolve(ProductType.NORMAL);
        verify(processorResolver).resolve(ProductType.EXPIRABLE);
        verify(processor).process(cable);
        verify(processor).process(milk);
    }

    @Test
    void failsWhenTheOrderIsUnknown() {
        when(orderRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.processOrder(404L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("404");
    }

    private static Order order(Long id, Product... items) {
        Order order = new Order();
        order.setId(id);
        order.setItems(Set.of(items));
        return order;
    }

    private static Product product(ProductType type, String name) {
        return Product.builder().type(type).name(name).available(10).leadTime(5).build();
    }
}
