package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
public class OrdersControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    // ==========================================
    // NORMAL PRODUCTS
    // ==========================================

    @Test
    public void processOrder_NormalProduct_InStock_ShouldDecrement() throws Exception {
        Product p = createAndSaveProduct("NORMAL", 10, 5, null, null, null);
        Order order = createAndSaveOrder(Set.of(p));

        mockMvc.perform(post("/orders/{orderId}/processOrder", order.getId()))
                .andExpect(status().isOk());

        Product updatedProduct = productRepository.findById(p.getId()).get();
        assertEquals(9, updatedProduct.getAvailable());
        verifyNoInteractions(notificationService);
    }

    @Test
    public void processOrder_NormalProduct_OutOfStock_ShouldNotifyDelay() throws Exception {
        Product p = createAndSaveProduct("NORMAL", 0, 5, null, null, null);
        Order order = createAndSaveOrder(Set.of(p));

        mockMvc.perform(post("/orders/{orderId}/processOrder", order.getId()))
                .andExpect(status().isOk());

        Product updatedProduct = productRepository.findById(p.getId()).get();
        assertEquals(0, updatedProduct.getAvailable()); // Stock unchanged
        verify(notificationService, times(1)).sendDelayNotification(5, p.getName());
    }

    // ==========================================
    // SEASONAL PRODUCTS
    // ==========================================

    @Test
    public void processOrder_SeasonalProduct_InSeasonAndInStock_ShouldDecrement() throws Exception {
        Product p = createAndSaveProduct("SEASONAL", 10, 5, null,
                LocalDate.now().minusDays(10), LocalDate.now().plusDays(10));
        Order order = createAndSaveOrder(Set.of(p));

        mockMvc.perform(post("/orders/{orderId}/processOrder", order.getId()))
                .andExpect(status().isOk());

        Product updatedProduct = productRepository.findById(p.getId()).get();
        assertEquals(9, updatedProduct.getAvailable());
        verifyNoInteractions(notificationService);
    }

    @Test
    public void processOrder_SeasonalProduct_LeadTimeExceedsSeason_ShouldNotifyOutOfStock() throws Exception {
        // SET AVAILABLE TO 0: Lead time is 15 days, but season ends in 5 days
        Product p = createAndSaveProduct("SEASONAL", 0, 15, null,
                LocalDate.now().minusDays(10), LocalDate.now().plusDays(5));
        Order order = createAndSaveOrder(Set.of(p));

        mockMvc.perform(post("/orders/{orderId}/processOrder", order.getId()))
                .andExpect(status().isOk());

        Product updatedProduct = productRepository.findById(p.getId()).get();
        assertEquals(0, updatedProduct.getAvailable());
        verify(notificationService, times(1)).sendOutOfStockNotification(p.getName());
    }

    // ==========================================
    // EXPIRABLE PRODUCTS
    // ==========================================

    @Test
    public void processOrder_ExpirableProduct_NotExpiredAndInStock_ShouldDecrement() throws Exception {
        Product p = createAndSaveProduct("EXPIRABLE", 10, 5, LocalDate.now().plusDays(5), null, null);
        Order order = createAndSaveOrder(Set.of(p));

        mockMvc.perform(post("/orders/{orderId}/processOrder", order.getId()))
                .andExpect(status().isOk());

        Product updatedProduct = productRepository.findById(p.getId()).get();
        assertEquals(9, updatedProduct.getAvailable());
        verifyNoInteractions(notificationService);
    }

    @Test
    public void processOrder_ExpirableProduct_Expired_ShouldNotifyExpiration() throws Exception {
        Product p = createAndSaveProduct("EXPIRABLE", 10, 5, LocalDate.now().minusDays(1), null, null);
        Order order = createAndSaveOrder(Set.of(p));

        mockMvc.perform(post("/orders/{orderId}/processOrder", order.getId()))
                .andExpect(status().isOk());

        Product updatedProduct = productRepository.findById(p.getId()).get();
        assertEquals(0, updatedProduct.getAvailable()); // Hamid's code forces this to 0
        verify(notificationService, times(1)).sendExpirationNotification(p.getName(), p.getExpiryDate());
    }


    // ==========================================
    // HELPER METHODS
    // ==========================================

    private Product createAndSaveProduct(String type, int available, int leadTime, LocalDate expiry, LocalDate seasonStart, LocalDate seasonEnd) {
        Product p = new Product(null, leadTime, available, type, "Test Product - " + type, expiry, seasonStart, seasonEnd);
        return productRepository.save(p);
    }

    private Order createAndSaveOrder(Set<Product> products) {
        Order order = new Order();
        order.setItems(products);
        return orderRepository.save(order);
    }
}