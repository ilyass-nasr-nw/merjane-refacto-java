package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.handlers.ProductTypeHandler;
import com.nimbleways.springboilerplate.services.handlers.ProductTypeHandlerFactory;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@UnitTest
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductTypeHandlerFactory productTypeHandlerFactory;

    @Mock
    private ProductTypeHandler productTypeHandler;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldProcessOrderSuccessfully() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);

        Product product1 = new Product(1L, 15, 30, "NORMAL", "USB Cable", null, null, null);
        Product product2 = new Product(2L, 10, 5, "NORMAL", "Mouse", null, null, null);
        Set<Product> products = new HashSet<>();
        products.add(product1);
        products.add(product2);
        order.setItems(products);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(productTypeHandlerFactory.getHandler("NORMAL")).thenReturn(productTypeHandler);

        ProcessOrderResponse response = orderService.processOrder(orderId);

        assertNotNull(response);
        assertEquals(orderId, response.id());
        verify(orderRepository, times(1)).findById(orderId);
        verify(productTypeHandler, times(2)).processProductOrder(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(
            OrderNotFoundException.class,
            () -> orderService.processOrder(orderId)
        );

        assertTrue(exception.getMessage().contains("Order not found: 999"));
        verify(orderRepository, times(1)).findById(orderId);
        verifyNoInteractions(productTypeHandlerFactory);
    }

    @Test
    void shouldProcessOrderWithMultipleProductTypes() {
        Long orderId = 2L;
        Order order = new Order();
        order.setId(orderId);

        Product normalProduct = new Product(1L, 15, 30, "NORMAL", "USB Cable", null, null, null);
        Product seasonalProduct = new Product(2L, 10, 5, "SEASONAL", "Watermelon", null, null, null);
        Product expirableProduct = new Product(3L, 5, 10, "EXPIRABLE", "Milk", null, null, null);

        Set<Product> products = new HashSet<>();
        products.add(normalProduct);
        products.add(seasonalProduct);
        products.add(expirableProduct);
        order.setItems(products);

        ProductTypeHandler normalHandler = mock(ProductTypeHandler.class);
        ProductTypeHandler seasonalHandler = mock(ProductTypeHandler.class);
        ProductTypeHandler expirableHandler = mock(ProductTypeHandler.class);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(productTypeHandlerFactory.getHandler("NORMAL")).thenReturn(normalHandler);
        when(productTypeHandlerFactory.getHandler("SEASONAL")).thenReturn(seasonalHandler);
        when(productTypeHandlerFactory.getHandler("EXPIRABLE")).thenReturn(expirableHandler);

        ProcessOrderResponse response = orderService.processOrder(orderId);

        assertNotNull(response);
        assertEquals(orderId, response.id());
        verify(normalHandler, times(1)).processProductOrder(normalProduct);
        verify(seasonalHandler, times(1)).processProductOrder(seasonalProduct);
        verify(expirableHandler, times(1)).processProductOrder(expirableProduct);
    }

    @Test
    void shouldProcessEmptyOrder() {
        Long orderId = 3L;
        Order order = new Order();
        order.setId(orderId);
        order.setItems(new HashSet<>());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ProcessOrderResponse response = orderService.processOrder(orderId);

        assertNotNull(response);
        assertEquals(orderId, response.id());
        verify(orderRepository, times(1)).findById(orderId);
        verifyNoInteractions(productTypeHandlerFactory);
    }
}
