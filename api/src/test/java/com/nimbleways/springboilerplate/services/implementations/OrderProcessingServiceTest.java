package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.services.OrderProcessingService;
import com.nimbleways.springboilerplate.strategies.ProductAvailabilityStrategy;
import com.nimbleways.springboilerplate.strategies.ProductStrategyFactory;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@UnitTest
@DisplayName("OrderProcessingService Tests")
class OrderProcessingServiceTest {

    @Mock
    private ProductStrategyFactory productStrategyFactory;

    @Mock
    private ProductAvailabilityStrategy mockStrategy;

    private OrderProcessingService orderProcessingService;

    @BeforeEach
    void setUp() {
        orderProcessingService = new OrderProcessingService(productStrategyFactory);
    }

    @Test
    @DisplayName("Should process all products in an order")
    void shouldProcessAllProductsInOrder() {
        Product product1 = createProduct("Product 1", "NORMAL");
        Product product2 = createProduct("Product 2", "SEASONAL");
        Product product3 = createProduct("Product 3", "EXPIRABLE");

        Set<Product> products = Set.of(product1, product2, product3);
        Order order = createOrder(1L, products);

        when(productStrategyFactory.getStrategy(any(ProductType.class)))
                .thenReturn(mockStrategy);

        orderProcessingService.processOrder(order);

        verify(mockStrategy, times(3)).processOrder(any(Product.class));
        verify(productStrategyFactory, times(3)).getStrategy(any(ProductType.class));
    }

    @Test
    @DisplayName("Should handle null order gracefully")
    void shouldHandleNullOrderGracefully() {
        orderProcessingService.processOrder(null);

        verifyNoInteractions(productStrategyFactory);
        verifyNoInteractions(mockStrategy);
    }

    @Test
    @DisplayName("Should handle order with null items gracefully")
    void shouldHandleOrderWithNullItemsGracefully() {
        Order order = new Order();
        order.setId(1L);
        order.setItems(null);

        orderProcessingService.processOrder(order);

        verifyNoInteractions(productStrategyFactory);
        verifyNoInteractions(mockStrategy);
    }

    @Test
    @DisplayName("Should handle empty order")
    void shouldHandleEmptyOrder() {
        Order order = createOrder(1L, new HashSet<>());

        orderProcessingService.processOrder(order);

        verifyNoInteractions(productStrategyFactory);
        verifyNoInteractions(mockStrategy);
    }

    @Test
    @DisplayName("Should throw exception for invalid product type")
    void shouldThrowExceptionForInvalidProductType() {
        Product product = createProduct("Invalid Product", "INVALID_TYPE");
        Order order = createOrder(1L, Set.of(product));

        assertThrows(IllegalArgumentException.class,
                () -> orderProcessingService.processOrder(order));
    }

    @Test
    @DisplayName("Should throw exception for null product type")
    void shouldThrowExceptionForNullProductType() {
        Product product = new Product();
        product.setName("Test Product");
        product.setType(null);

        Order order = createOrder(1L, Set.of(product));

        assertThrows(IllegalArgumentException.class,
                () -> orderProcessingService.processOrder(order));
    }

    @Test
    @DisplayName("Should handle order with mixed product types")
    void shouldHandleOrderWithMixedProductTypes() {
        Product normalProduct = createProduct("USB Cable", "NORMAL");
        Product seasonalProduct = createProduct("Watermelon", "SEASONAL");
        Product expirableProduct = createProduct("Milk", "EXPIRABLE");

        Order order = createOrder(1L, Set.of(normalProduct, seasonalProduct, expirableProduct));

        when(productStrategyFactory.getStrategy(ProductType.NORMAL)).thenReturn(mockStrategy);
        when(productStrategyFactory.getStrategy(ProductType.SEASONAL)).thenReturn(mockStrategy);
        when(productStrategyFactory.getStrategy(ProductType.EXPIRABLE)).thenReturn(mockStrategy);

        orderProcessingService.processOrder(order);

        verify(mockStrategy, times(3)).processOrder(any(Product.class));
        verify(productStrategyFactory).getStrategy(ProductType.NORMAL);
        verify(productStrategyFactory).getStrategy(ProductType.SEASONAL);
        verify(productStrategyFactory).getStrategy(ProductType.EXPIRABLE);
    }

    @Test
    @DisplayName("Should handle case-insensitive product types")
    void shouldHandleCaseInsensitiveProductTypes() {
        Product product1 = createProduct("Product 1", "normal");
        Product product2 = createProduct("Product 2", "SEASONAL");
        Product product3 = createProduct("Product 3", "ExPiRaBlE");

        Order order = createOrder(1L, Set.of(product1, product2, product3));

        when(productStrategyFactory.getStrategy(any(ProductType.class)))
                .thenReturn(mockStrategy);

        orderProcessingService.processOrder(order);

        verify(mockStrategy, times(3)).processOrder(any(Product.class));
    }

    private Product createProduct(String name, String type) {
        Product product = new Product();
        product.setName(name);
        product.setType(type);
        product.setAvailable(10);
        return product;
    }

    private Order createOrder(Long id, Set<Product> items) {
        Order order = new Order();
        order.setId(id);
        order.setItems(items);
        return order;
    }
}