package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.enums.ProductType;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@UnitTest
public class OrderServiceImplTest {

    @Mock
    private com.nimbleways.springboilerplate.services.implementations.ProductServiceImpl productService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private com.nimbleways.springboilerplate.services.implementations.OrderServiceImpl orderService;

    @Test
    void processOrderShouldProcessProductsAndReturnSuccessResponse() {
        Long orderId = 1L;
        Set<Product> products = Set.of(new Product(10L, 5, 2, ProductType.NORMAL, "", null, null, null));

        Order order = new Order(orderId, products);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ProcessOrderResponse response = orderService.processOrder(orderId);

        verify(productService, times(1)).process(products);
        assertEquals(orderId, response.id());
    }

    @Test
    void processOrderShouldThrowWhenOrderDoesNotExist() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> orderService.processOrder(orderId));

        verifyNoInteractions(productService);
        assertEquals("Order with ID 1 not found.", exception.getMessage());
    }
}
