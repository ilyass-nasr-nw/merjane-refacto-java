package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.order.Order;
import com.nimbleways.springboilerplate.entities.product.NormalProduct;
import com.nimbleways.springboilerplate.entities.product.Product;
import com.nimbleways.springboilerplate.entities.product.SeasonalProduct;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.product.ProductService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@UnitTest
class OrderServiceImplTest {
    private OrderRepository orderRepository;
    private ProductService productService;
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        productService = mock(ProductService.class);
        orderService = new OrderServiceImpl(orderRepository, productService);
    }

    @Test
    void processOrder_shouldProcessProducts_whenOrderHasProducts() {

        Long orderId = 10L;

        Product normalProduct = mock(NormalProduct.class);
        Product seasonalProduct = mock(SeasonalProduct.class);

        Order order = mock(Order.class);
        when(order.getId()).thenReturn(orderId);
        when(order.getItems()).thenReturn(Set.of(normalProduct, seasonalProduct));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ProcessOrderResponse response = orderService.processOrder(orderId);

        assertThat(response.id()).isEqualTo(orderId);

        verify(productService, times(1)).processProduct(normalProduct);
        verify(productService, times(1)).processProduct(seasonalProduct);
    }

    @Test
    void processOrder_shouldReturnResponse_whenOrderHasNoProducts() {

        Long orderId = 20L;

        Order order = mock(Order.class);
        when(order.getId()).thenReturn(orderId);
        when(order.getItems()).thenReturn(Set.of());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ProcessOrderResponse response = orderService.processOrder(orderId);

        assertThat(response.id()).isEqualTo(orderId);

        verify(productService, never()).processProduct(ArgumentMatchers.any());
    }

    @Test
    void processOrder_shouldThrow_whenOrderNotFound() {

        Long orderId = 99L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> orderService.processOrder(orderId));

        verify(productService, never()).processProduct(any());
    }
}
