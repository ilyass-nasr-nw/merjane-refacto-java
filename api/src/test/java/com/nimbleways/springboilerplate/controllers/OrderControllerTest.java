package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.contollers.OrderController;
import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.OrderProcessingService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@UnitTest
@DisplayName("OrderController Unit Tests")
class OrderControllerTest {

    @Mock
    private OrderProcessingService orderProcessingService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderController orderController;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setItems(new HashSet<>());
    }

    @Test
    @DisplayName("Should successfully process an order when order exists")
    void shouldSuccessfullyProcessOrderWhenOrderExists() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));

        ProcessOrderResponse response = orderController.processOrder(orderId);

        assertNotNull(response);
        assertEquals(orderId, response.id());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderProcessingService, times(1)).processOrder(testOrder);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when order not found")
    void shouldThrowExceptionWhenOrderNotFound() {
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderController.processOrder(orderId)
        );

        assertTrue(exception.getMessage().contains("Order not found with id: 999"));
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderProcessingService, never()).processOrder(any());
    }

    @Test
    @DisplayName("Should handle null order ID gracefully")
    void shouldHandleNullOrderIdGracefully() {
        Long orderId = null;

        assertThrows(Exception.class, () -> orderController.processOrder(orderId));
        verify(orderProcessingService, never()).processOrder(any());
    }

    @Test
    @DisplayName("Should propagate service exceptions")
    void shouldPropagateServiceExceptions() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        doThrow(new RuntimeException("Service error"))
                .when(orderProcessingService).processOrder(any());

        assertThrows(RuntimeException.class,
                () -> orderController.processOrder(orderId));

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderProcessingService, times(1)).processOrder(testOrder);
    }

    @Test
    @DisplayName("Should process order with zero ID")
    void shouldProcessOrderWithZeroId() {
        Long orderId = 0L;
        Order orderWithZeroId = new Order();
        orderWithZeroId.setId(0L);
        orderWithZeroId.setItems(new HashSet<>());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderWithZeroId));

        ProcessOrderResponse response = orderController.processOrder(orderId);

        assertNotNull(response);
        assertEquals(0L, response.id());
        verify(orderProcessingService, times(1)).processOrder(orderWithZeroId);
    }

    @Test
    @DisplayName("Should process order with large ID")
    void shouldProcessOrderWithLargeId() {
        Long orderId = Long.MAX_VALUE;
        Order orderWithLargeId = new Order();
        orderWithLargeId.setId(orderId);
        orderWithLargeId.setItems(new HashSet<>());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderWithLargeId));

        ProcessOrderResponse response = orderController.processOrder(orderId);

        assertNotNull(response);
        assertEquals(orderId, response.id());
        verify(orderProcessingService, times(1)).processOrder(orderWithLargeId);
    }

    @Test
    @DisplayName("Should handle order with null items")
    void shouldHandleOrderWithNullItems() {
        Long orderId = 1L;
        Order orderWithNullItems = new Order();
        orderWithNullItems.setId(orderId);
        orderWithNullItems.setItems(null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderWithNullItems));

        ProcessOrderResponse response = orderController.processOrder(orderId);

        assertNotNull(response);
        assertEquals(orderId, response.id());
        verify(orderProcessingService, times(1)).processOrder(orderWithNullItems);
    }

    @Test
    @DisplayName("Should verify service is called exactly once per request")
    void shouldVerifyServiceCalledExactlyOnce() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));

        orderController.processOrder(orderId);

        verify(orderProcessingService, times(1)).processOrder(testOrder);
        verify(orderProcessingService, only()).processOrder(any());
    }
}