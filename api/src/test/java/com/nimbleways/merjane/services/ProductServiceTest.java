package com.nimbleways.merjane.services;

import com.nimbleways.merjane.persistence.entities.Order;
import com.nimbleways.merjane.persistence.entities.Product;
import com.nimbleways.merjane.domain.ProductType;
import com.nimbleways.merjane.persistence.repositories.OrderRepository;
import com.nimbleways.merjane.services.handlers.ProductTypeHandler;
import com.nimbleways.merjane.utils.Annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@UnitTest
public class ProductServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductTypeHandler normalHandler;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        Mockito.when(normalHandler.getSupportedType()).thenReturn(ProductType.NORMAL);
        productService = new ProductService();
        ReflectionTestUtils.setField(productService, "orderRepository", orderRepository);
        ReflectionTestUtils.setField(productService, "handlers", List.of(normalHandler));
    }

    @Test
    public void processOrder_dispatchesToMatchingHandler_andReturnsOrderId() {
        Product product = new Product(null, 15, 10, ProductType.NORMAL, "USB Cable", null, null, null);
        Order order = new Order(1L, Set.of(product));
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Long result = productService.processOrder(1L);

        assertEquals(1L, result);
        Mockito.verify(normalHandler).handle(product);
    }

    @Test
    public void processOrder_noMatchingHandler_skipsProduct() {
        Product product = new Product(null, 15, 10, ProductType.SEASONAL, "Watermelon", null, null, null);
        Order order = new Order(1L, Set.of(product));
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Long result = productService.processOrder(1L);

        assertEquals(1L, result);
        Mockito.verify(normalHandler, Mockito.never()).handle(product);
    }

    @Test
    public void processOrder_orderNotFound_throwsException() {
        Mockito.when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> productService.processOrder(99L));
    }
}
