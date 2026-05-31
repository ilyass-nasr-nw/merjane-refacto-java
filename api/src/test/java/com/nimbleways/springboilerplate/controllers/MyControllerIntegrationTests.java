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

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MyControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    // =====================================================
    // BASE DATASET (YOUR METHOD)
    // =====================================================
    private List<Product> createProducts() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(null, 30, 15, "NORMAL", "USB Cable", null, null, null));
        products.add(new Product(null, 0, 10, "NORMAL", "USB Dongle", null, null, null));

        products.add(new Product(null, 30, 15, "EXPIRABLE", "Butter",
                LocalDate.now().plusDays(26), null, null));

        products.add(new Product(null, 6, 90, "EXPIRABLE", "Milk",
                LocalDate.now().minusDays(2), null, null));

        products.add(new Product(null, 30, 15, "SEASONAL", "Watermelon",
                null,
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(58)));

        products.add(new Product(null, 30, 15, "SEASONAL", "Grapes",
                null,
                LocalDate.now().plusDays(180),
                LocalDate.now().plusDays(240)));

        return products;
    }

    private Order createOrder(Set<Product> products) {
        Order order = new Order();
        order.setItems(products);
        return order;
    }

    private void processOrder(Long orderId) throws Exception {
        mockMvc.perform(post("/orders/{orderId}/processOrder", orderId))
                .andExpect(status().isOk());
    }

    // =====================================================
    // NORMAL PRODUCT
    // =====================================================

    @Test
    void shouldDecrementAvailableForNormalProduct() throws Exception {

        List<Product> products = createProducts();
        Product usbCable = products.get(0);

        productRepository.save(usbCable);

        Order order = orderRepository.save(createOrder(Set.of(usbCable)));

        processOrder(order.getId());

        Product updated = productRepository.findById(usbCable.getId()).orElseThrow();

        assertEquals(14, updated.getAvailable());
    }

    @Test
    void shouldNotifyDelayWhenNormalProductOutOfStock() throws Exception {

        List<Product> products = createProducts();
        Product usbDongle = products.get(1);

        productRepository.save(usbDongle);

        Order order = orderRepository.save(createOrder(Set.of(usbDongle)));

        processOrder(order.getId());

        verify(notificationService)
                .sendDelayNotification(0, "USB Dongle");
    }	

    // =====================================================
    // EXPIRABLE PRODUCT
    // =====================================================

    @Test
    void shouldSellNonExpiredProduct() throws Exception {

        List<Product> products = createProducts();
        Product butter = products.get(2);

        productRepository.save(butter);

        Order order = orderRepository.save(createOrder(Set.of(butter)));

        processOrder(order.getId());

        Product updated = productRepository.findById(butter.getId()).orElseThrow();

        assertEquals(14, updated.getAvailable());
    }

    @Test
    void shouldHandleExpiredProduct() throws Exception {

        List<Product> products = createProducts();
        Product milk = products.get(3);

        productRepository.save(milk);

        Order order = orderRepository.save(createOrder(Set.of(milk)));

        processOrder(order.getId());

        Product updated = productRepository.findById(milk.getId()).orElseThrow();

        assertEquals(0, updated.getAvailable());

        verify(notificationService)
                .sendExpirationNotification("Milk", milk.getExpiryDate());
    }

    // =====================================================
    // SEASONAL PRODUCT
    // =====================================================

    @Test
    void shouldSellSeasonalProductDuringSeason() throws Exception {

        List<Product> products = createProducts();
        Product watermelon = products.get(4);

        productRepository.save(watermelon);

        Order order = orderRepository.save(createOrder(Set.of(watermelon)));

        processOrder(order.getId());

        Product updated = productRepository.findById(watermelon.getId()).orElseThrow();

        assertEquals(14, updated.getAvailable());
    }

    @Test
    void shouldNotSellSeasonalProductOutsideSeason() throws Exception {

        List<Product> products = createProducts();
        Product grapes = products.get(5);

        productRepository.save(grapes);

        Order order = orderRepository.save(createOrder(Set.of(grapes)));

        processOrder(order.getId());

        Product updated = productRepository.findById(grapes.getId()).orElseThrow();

        assertEquals(15, updated.getAvailable());

        verify(notificationService)
                .sendOutOfStockNotification("Grapes");
    }

    // =====================================================
    // FULL SCENARIO
    // =====================================================

    @Test
    void shouldProcessAllProductsAccordingToBusinessRules() throws Exception {

        List<Product> products = createProducts();

        productRepository.saveAll(products);

        Order order = orderRepository.save(createOrder(new HashSet<>(products)));

        processOrder(order.getId());

        Product usbCable = productRepository.findById(products.get(0).getId()).orElseThrow();
        Product butter = productRepository.findById(products.get(2).getId()).orElseThrow();
        Product watermelon = productRepository.findById(products.get(4).getId()).orElseThrow();

        assertEquals(14, usbCable.getAvailable());
        assertEquals(14, butter.getAvailable());
        assertEquals(14, watermelon.getAvailable());
    }
}