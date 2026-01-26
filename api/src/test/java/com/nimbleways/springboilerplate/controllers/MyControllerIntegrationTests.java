package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.entities.*;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Specify the controller class you want to test
// This indicates to spring boot to only load UsersController into the context
// Which allows a better performance and needs to do less mocks
@SpringBootTest
@AutoConfigureMockMvc
public class MyControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void processOrderShouldReturn() throws Exception {
        List<Product> allProducts = createProducts();
        Set<Product> orderItems = new HashSet<Product>(allProducts);
        Order order = createOrder(orderItems);
        productRepository.saveAll(allProducts);
        order = orderRepository.save(order);
        mockMvc.perform(post("/api/v1/orders/{orderId}/processOrder", order.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk());
        Order resultOrder = orderRepository.findById(order.getId()).get();
        assertEquals(resultOrder.getId(), order.getId());
    }

    private static Order createOrder(Set<Product> products) {
        Order order = new Order();
        order.setItems(products);
        return order;
    }

    private static List<Product> createProducts() {
        List<Product> products = new ArrayList<>();

        products.add(new NormalProduct(null, 15, 30, ProductType.NORMAL, "USB Cable"));
        products.add(new NormalProduct(null, 10, 0, ProductType.NORMAL, "USB Dongle"));

        products.add(new ExpirableProduct(null, 15, 30, ProductType.EXPIRABLE, "Butter", LocalDate.now().plusDays(26)));
        products.add(new ExpirableProduct(null, 90, 6, ProductType.EXPIRABLE, "Milk", LocalDate.now().minusDays(2)));

        products.add(new SeasonalProduct(null, 15, 30, ProductType.SEASONAL, "Watermelon", LocalDate.now().minusDays(2), LocalDate.now().plusDays(58)));
        products.add(new SeasonalProduct(null, 15, 30, ProductType.SEASONAL, "Grapes", LocalDate.now().plusDays(180), LocalDate.now().plusDays(240)));

        return products;

    }
}