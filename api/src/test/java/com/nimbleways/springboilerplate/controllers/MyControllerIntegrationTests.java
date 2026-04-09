package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// import com.fasterxml.jackson.databind.ObjectMapper;

// Specify the controller class you want to test
// This indicates to spring boot to only load UsersController into the context
// Which allows a better performance and needs to do less mocks
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MyControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationServiceImpl notificationService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void processOrderWithNormalProductAndAvailabilityGreaterThan0ShouldReturn() throws Exception {
        Product phone = new Product(
                null,
                10,
                5,
                ProductType.NORMAL,
                "phone",
                null,
                null,
                null);
        productRepository.save(phone);

        Order order = new Order();
        order.setItems(Set.of(phone));
        order = orderRepository.save(order);

        mockMvc.perform(post("/orders/{orderId}/processOrder", order.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk());

        Order resultOrder = orderRepository.findById(order.getId()).orElseThrow();

        Assertions.assertEquals(4, Objects.requireNonNull(resultOrder.getItems()
                        .stream()
                        .findFirst()
                        .orElse(null))
                .getAvailable().intValue());
        verify(notificationService, times(1)).sendDelayNotification(anyInt(), anyString());
    }

    @Test
    public void processOrderWithNormalProductAndAvailabilityLesserThan0ShouldReturn() throws Exception {
        Product phone = new Product(
                null,
                10,
                0,
                ProductType.NORMAL,
                "phone",
                null,
                null,
                null);
        productRepository.save(phone);

        Order order = new Order();
        order.setItems(Set.of(phone));
        order = orderRepository.save(order);

        mockMvc.perform(post("/orders/{orderId}/processOrder", order.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk());

        Order resultOrder = orderRepository.findById(order.getId()).orElseThrow();

        Assertions.assertEquals(0, Objects.requireNonNull(resultOrder.getItems()
                        .stream()
                        .findFirst()
                        .orElse(null))
                .getAvailable().intValue());
        verify(notificationService, times(1)).sendDelayNotification(anyInt(), anyString());
    }

    @Test
    public void processOrderWithNormalProductAndAvailabilityGreaterThan0AndLeadTimeEqualTo0ShouldReturn() throws Exception {
        Product phone = new Product(
                null,
                0,
                10,
                ProductType.NORMAL,
                "phone",
                null,
                null,
                null);
        productRepository.save(phone);

        Order order = new Order();
        order.setItems(Set.of(phone));
        order = orderRepository.save(order);

        mockMvc.perform(post("/orders/{orderId}/processOrder", order.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk());

        Order resultOrder = orderRepository.findById(order.getId()).orElseThrow();

        Assertions.assertEquals(9, Objects.requireNonNull(resultOrder.getItems()
                        .stream()
                        .findFirst()
                        .orElse(null))
                .getAvailable().intValue());
        verify(notificationService, times(0)).sendDelayNotification(anyInt(), anyString());
    }


}

