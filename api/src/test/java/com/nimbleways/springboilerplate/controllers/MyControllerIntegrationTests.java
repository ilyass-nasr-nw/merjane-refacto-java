package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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
    public void processOrderShouldDecrementNormalProductWhenAvailable() throws Exception {
        Product result = processOrderWith(new Product(null, 15, 30, "NORMAL", "USB Cable", null, null, null));

        assertThat(result.getAvailable()).isEqualTo(29);
        verifyNoInteractions(notificationService);
    }

    @Test
    public void processOrderShouldNotifyNormalProductDelayWhenOutOfStock() throws Exception {
        Product result = processOrderWith(new Product(null, 10, 0, "NORMAL", "USB Dongle", null, null, null));

        assertThat(result.getAvailable()).isEqualTo(0);
        assertThat(result.getLeadTime()).isEqualTo(10);
        verify(notificationService).sendDelayNotification(10, "USB Dongle");
    }

    @Test
    public void processOrderShouldDecrementSeasonalProductDuringSeason() throws Exception {
        LocalDate today = LocalDate.now();
        Product result = processOrderWith(new Product(
                null, 15, 30, "SEASONAL", "Watermelon", null, today.minusDays(2), today.plusDays(58)));

        assertThat(result.getAvailable()).isEqualTo(29);
        verifyNoInteractions(notificationService);
    }

    @Test
    public void processOrderShouldNotifySeasonalProductUnavailableWhenRestockExceedsSeason() throws Exception {
        LocalDate today = LocalDate.now();
        Product result = processOrderWith(new Product(
                null, 15, 0, "SEASONAL", "Grapes", null, today.minusDays(2), today.plusDays(3)));

        assertThat(result.getAvailable()).isEqualTo(0);
        verify(notificationService).sendOutOfStockNotification("Grapes");
    }

    @Test
    public void processOrderShouldDecrementExpirableProductBeforeExpiryDate() throws Exception {
        Product result = processOrderWith(new Product(
                null, 15, 30, "EXPIRABLE", "Butter", LocalDate.now().plusDays(26), null, null));

        assertThat(result.getAvailable()).isEqualTo(29);
        verifyNoInteractions(notificationService);
    }

    @Test
    public void processOrderShouldNotifyExpiredProductAndMarkUnavailable() throws Exception {
        LocalDate expiryDate = LocalDate.now().minusDays(2);
        Product result = processOrderWith(new Product(null, 90, 6, "EXPIRABLE", "Milk", expiryDate, null, null));

        assertThat(result.getAvailable()).isEqualTo(0);
        verify(notificationService).sendExpirationNotification("Milk", expiryDate);
    }

    private Product processOrderWith(Product product) throws Exception {
        Product savedProduct = productRepository.save(product);
        Order savedOrder = orderRepository.save(createOrder(Set.of(savedProduct)));

        mockMvc.perform(post("/orders/{orderId}/processOrder", savedOrder.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId()));

        Optional<Product> result = productRepository.findById(savedProduct.getId());
        return result.orElseThrow();
    }

    private static Order createOrder(Set<Product> products) {
        Order order = new Order();
        order.setItems(new HashSet<>(products));
        return order;
    }
}
