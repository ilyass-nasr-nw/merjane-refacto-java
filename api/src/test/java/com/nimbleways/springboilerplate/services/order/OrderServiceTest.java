package com.nimbleways.springboilerplate.services.order;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.NormalProduct;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.product.ProductService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@UnitTest
public class OrderServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private Product product1;
    private Product product2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        product1 = new NormalProduct(1L, 10, 5, ProductType.NORMAL, "Product1");
        product2 = new NormalProduct(2L, 5, 3, ProductType.NORMAL, "Product2");

        Set<Product> products = new HashSet<>();
        products.add(product1);
        products.add(product2);

        order = new Order();
        order.setId(1L);
        order.setItems(products);
    }

    @Test
    public void shouldProcessOrderSuccessfully() throws OrderNotFoundException {
        // GIVEN
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(productService).processProduct(any(Product.class));

        // WHEN
        ProcessOrderResponse response = orderService.processOrder(1L);

        // THEN
        assertEquals(1L, response.id());
        verify(orderRepository, times(1)).findById(1L);
        verify(productService, times(2)).processProduct(any(Product.class));
    }

    @Test
    public void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {
        // GIVEN
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.processOrder(1L));
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void shouldHandleOrderWithNoProducts() throws OrderNotFoundException {
        // GIVEN
        Order emptyOrder = new Order();
        emptyOrder.setId(2L);
        emptyOrder.setItems(new HashSet<>());
        when(orderRepository.findById(2L)).thenReturn(Optional.of(emptyOrder));

        // WHEN
        ProcessOrderResponse response = orderService.processOrder(2L);

        // THEN
        assertEquals(2L, response.id());
        verify(orderRepository, times(1)).findById(2L);
    }

}
