package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderServiceUnitTest {

    @Mock
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void shouldPassOrderWhenNormalProductIsAvailable() {
        // GIVEN
        Product product = Product.builder()
                .id(1L)
                .available(10)
                .name("NormalProduct")
                .type(ProductType.NORMAL)
                .build();
        Order order = new Order(1L, Set.of(product));

        Mockito.when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // WHEN
        var orderResponse =  orderService.processOrder(order.getId());

        // THEN
        assertEquals(orderResponse.id(),order.getId());
        assertEquals(9,product.getAvailable());
        Mockito.verify(productRepository,Mockito.times(1)).save(product);
    }
    @Test
    public void shouldNotifyUserWhenNormalProductIsAvailable() {
        // GIVEN
        Product product = Product.builder()
                .id(1L)
                .available(0)
                .name("NormalProduct")
                .type(ProductType.NORMAL)
                .build();
        Order order = new Order(1L, Set.of(product));

        Mockito.when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // WHEN
        orderService.processOrder(order.getId());

        // THEN
        assertEquals(10,product.getAvailable());
        Mockito.verify(productRepository,Mockito.times(0)).save(product);
    }
}