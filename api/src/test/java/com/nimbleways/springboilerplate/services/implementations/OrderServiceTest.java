package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enumerations.ProductTypeEnum;
import com.nimbleways.springboilerplate.exceptions.FunctionalException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(null, 15, 10, ProductTypeEnum.NORMAL, "RJ45 Cable", null, null, null);
        Set<Product> items = new HashSet<>();
        items.add(product);
        order = new Order();
        order.setId(1L);
        order.setItems(items);
    }

    @Test
    void testProcessOrder_Success() throws FunctionalException {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        ProcessOrderResponse response = orderService.processOrder(1L);

        verify(productService, times(1)).updateProducts(anyList());
        assertEquals(1L, response.id());
    }

    @Test
    void testProcessOrder_OrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        FunctionalException exception = assertThrows(FunctionalException.class, () -> orderService.processOrder(1L));

        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    void testHandleProductByType_Normal() {
        List<Product> productsToSave = new ArrayList<>();
        orderService.handleProductByType(product, productsToSave);

        verify(productService, times(1)).handleNormalProduct(product, productsToSave);
    }

    @Test
    void testHandleProductByType_Seasonal() {
        product.setType(ProductTypeEnum.SEASONAL);
        List<Product> productsToSave = new ArrayList<>();
        orderService.handleProductByType(product, productsToSave);

        verify(productService, times(1)).handleSeasonalProduct(product, productsToSave);
    }

    @Test
    void testHandleProductByType_Expirable() {
        product.setType(ProductTypeEnum.EXPIRABLE);
        List<Product> productsToSave = new ArrayList<>();
        orderService.handleProductByType(product, productsToSave);

        verify(productService, times(1)).handleExpiredProduct(product, productsToSave);
    }
}