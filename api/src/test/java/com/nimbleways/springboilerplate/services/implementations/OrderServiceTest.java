package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@UnitTest
class OrderServiceTest {

    @Mock
    private ProductService productService;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderService orderService;


    @Test
    void processOrderShouldReturn() {
        final Order order = createOrder(new HashSet<Product>((createProducts())));
        Mockito.when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertEquals(orderService.processOrder(order.getId()), order.getId());
    }

    private static Order createOrder(Set<Product> products) {
        Order order = new Order();
        order.setItems(products);
        return order;
    }

    private static List<Product> createProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(null, 15, 30, "NORMAL", "USB Cable", null, null, null));
        products.add(new Product(null, 10, 0, "NORMAL", "USB Dongle", null, null, null));
        products.add(new Product(null, 15, 30, "EXPIRABLE", "Butter", LocalDate.now().plusDays(26), null,
                null));
        products.add(new Product(null, 90, 6, "EXPIRABLE", "Milk", LocalDate.now().minusDays(2), null, null));
        products.add(new Product(null, 15, 30, "SEASONAL", "Watermelon", null, LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(58)));
        products.add(new Product(null, 15, 30, "SEASONAL", "Grapes", null, LocalDate.now().plusDays(180),
                LocalDate.now().plusDays(240)));
        return products;
    }
}