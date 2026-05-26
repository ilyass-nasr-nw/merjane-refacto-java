package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void processOrder_withMixedProducts_returnsOkAndUpdatesStock() throws Exception {
        List<Product> allProducts = createProducts();
        Set<Product> orderItems = new HashSet<>(allProducts);
        Order order = createOrder(orderItems);
        productRepository.saveAll(allProducts);
        order = orderRepository.save(order);

        mockMvc.perform(post("/orders/{orderId}/processOrder", order.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk());

        Order resultOrder = orderRepository.findById(order.getId()).get();
        assertEquals(resultOrder.getId(), order.getId());

        // Verify stock decremented for in-stock normal product
        Product usbCable = productRepository.findFirstByName("USB Cable").get();
        assertEquals(29, usbCable.getAvailable());

        // Verify delay notification for out-of-stock normal product
        verify(notificationService).sendDelayNotification(10, "USB Dongle");

        // Verify stock decremented for non-expired expirable product
        Product butter = productRepository.findFirstByName("Butter").get();
        assertEquals(29, butter.getAvailable());

        // Verify expiration notification for expired product
        verify(notificationService).sendExpirationNotification(
                org.mockito.Mockito.eq("Milk"),
                org.mockito.Mockito.any(LocalDate.class));
    }

    private static Order createOrder(Set<Product> products) {
        Order order = new Order();
        order.setItems(products);
        return order;
    }

    private static List<Product> createProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(null, 15, 30, ProductType.NORMAL, "USB Cable", null, null, null));
        products.add(new Product(null, 10, 0, ProductType.NORMAL, "USB Dongle", null, null, null));
        products.add(new Product(null, 15, 30, ProductType.EXPIRABLE, "Butter",
                LocalDate.now().plusDays(26), null, null));
        products.add(new Product(null, 90, 6, ProductType.EXPIRABLE, "Milk",
                LocalDate.now().minusDays(2), null, null));
        products.add(new Product(null, 15, 30, ProductType.SEASONAL, "Watermelon", null,
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(58)));
        products.add(new Product(null, 15, 30, ProductType.SEASONAL, "Grapes", null,
                LocalDate.now().plusDays(180), LocalDate.now().plusDays(240)));
        return products;
    }
}