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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTests {

        private static final String PROCESS_ORDER_URL = "/orders/{orderId}/process";
        private static final String APPLICATION_JSON = "application/json";
        private static final String TYPE_NORMAL = "NORMAL";
        private static final String TYPE_SEASONAL = "SEASONAL";
        private static final String TYPE_EXPIRABLE = "EXPIRABLE";

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private NotificationService notificationService;

        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private ProductRepository productRepository;

        @Test
        void shouldProcessOrderWithAllProductTypes() throws Exception {
                List<Product> allProducts = createAllProductTypes();
                Set<Product> orderItems = new HashSet<>(allProducts);
                Order order = createOrder(orderItems);
                productRepository.saveAll(allProducts);
                order = orderRepository.save(order);

                mockMvc.perform(post(PROCESS_ORDER_URL, order.getId())
                                .contentType(APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(order.getId()));

                Order resultOrder = orderRepository.findById(order.getId()).get();
                assertEquals(resultOrder.getId(), order.getId());
        }

        @Test
        void shouldReturn404WhenOrderNotFound() throws Exception {
                mockMvc.perform(post(PROCESS_ORDER_URL, 999L)
                                .contentType(APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.message").value("Order not found: 999"));
        }

        @Test
        void shouldDecrementNormalProductStock() throws Exception {
                Product product = new Product(null, 15, 10, TYPE_NORMAL, "USB Cable", null, null, null);
                product = productRepository.save(product);

                Order order = createOrder(Set.of(product));
                order = orderRepository.save(order);

                mockMvc.perform(post(PROCESS_ORDER_URL, order.getId())
                                .contentType(APPLICATION_JSON))
                                .andExpect(status().isOk());

                Product updatedProduct = productRepository.findById(product.getId()).get();
                assertEquals(9, updatedProduct.getAvailable());
                verifyNoInteractions(notificationService);
        }

        @Test
        void shouldNotifyDelayForOutOfStockNormalProduct() throws Exception {
                Product product = new Product(null, 15, 0, TYPE_NORMAL, "Mouse", null, null, null);
                product = productRepository.save(product);

                Order order = createOrder(Set.of(product));
                order = orderRepository.save(order);

                mockMvc.perform(post(PROCESS_ORDER_URL, order.getId())
                                .contentType(APPLICATION_JSON))
                                .andExpect(status().isOk());

                verify(notificationService, times(1)).sendDelayNotification(15, "Mouse");
        }

        @Test
        void shouldDecrementSeasonalProductWhenInSeason() throws Exception {
                LocalDate seasonStart = LocalDate.now().minusDays(10);
                LocalDate seasonEnd = LocalDate.now().plusDays(50);
                Product product = new Product(null, 15, 20, TYPE_SEASONAL, "Watermelon", null, seasonStart, seasonEnd);
                product = productRepository.save(product);

                Order order = createOrder(Set.of(product));
                order = orderRepository.save(order);

                mockMvc.perform(post(PROCESS_ORDER_URL, order.getId())
                                .contentType(APPLICATION_JSON))
                                .andExpect(status().isOk());

                Product updatedProduct = productRepository.findById(product.getId()).get();
                assertEquals(19, updatedProduct.getAvailable());
                verifyNoInteractions(notificationService);
        }

        @Test
        void shouldNotifyOutOfStockForSeasonalProductOutOfSeason() throws Exception {
                LocalDate seasonStart = LocalDate.now().plusDays(30);
                LocalDate seasonEnd = LocalDate.now().plusDays(90);
                Product product = new Product(null, 15, 10, TYPE_SEASONAL, "Grapes", null, seasonStart, seasonEnd);
                product = productRepository.save(product);

                Order order = createOrder(Set.of(product));
                order = orderRepository.save(order);

                mockMvc.perform(post(PROCESS_ORDER_URL, order.getId())
                                .contentType(APPLICATION_JSON))
                                .andExpect(status().isOk());

                verify(notificationService, times(1)).sendOutOfStockNotification("Grapes");
                Product updatedProduct = productRepository.findById(product.getId()).get();
                assertEquals(0, updatedProduct.getAvailable());
        }

        @Test
        void shouldDecrementExpirableProductWhenNotExpired() throws Exception {
                LocalDate expiryDate = LocalDate.now().plusDays(10);
                Product product = new Product(null, 15, 20, TYPE_EXPIRABLE, "Butter", expiryDate, null, null);
                product = productRepository.save(product);

                Order order = createOrder(Set.of(product));
                order = orderRepository.save(order);

                mockMvc.perform(post(PROCESS_ORDER_URL, order.getId())
                                .contentType(APPLICATION_JSON))
                                .andExpect(status().isOk());

                Product updatedProduct = productRepository.findById(product.getId()).get();
                assertEquals(19, updatedProduct.getAvailable());
                verifyNoInteractions(notificationService);
        }

        @Test
        void shouldNotifyExpirationForExpiredProduct() throws Exception {
                LocalDate expiryDate = LocalDate.now().minusDays(2);
                Product product = new Product(null, 90, 6, TYPE_EXPIRABLE, "Milk", expiryDate, null, null);
                product = productRepository.save(product);

                Order order = createOrder(Set.of(product));
                order = orderRepository.save(order);

                mockMvc.perform(post(PROCESS_ORDER_URL, order.getId())
                                .contentType(APPLICATION_JSON))
                                .andExpect(status().isOk());

                verify(notificationService, times(1)).sendExpirationNotification("Milk", expiryDate);
                Product updatedProduct = productRepository.findById(product.getId()).get();
                assertEquals(0, updatedProduct.getAvailable());
        }

        private static Order createOrder(Set<Product> products) {
                Order order = new Order();
                order.setItems(products);
                return order;
        }

        private static List<Product> createAllProductTypes() {
                List<Product> products = new ArrayList<>();
                products.add(new Product(null, 15, 30, TYPE_NORMAL, "USB Cable", null, null, null));
                products.add(new Product(null, 10, 0, TYPE_NORMAL, "USB Dongle", null, null, null));
                products.add(new Product(null, 15, 30, TYPE_EXPIRABLE, "Butter", LocalDate.now().plusDays(26), null, null));
                products.add(new Product(null, 90, 6, TYPE_EXPIRABLE, "Milk", LocalDate.now().minusDays(2), null, null));
                products.add(new Product(null, 15, 30, TYPE_SEASONAL, "Watermelon", null, LocalDate.now().minusDays(2),
                                LocalDate.now().plusDays(58)));
                products.add(new Product(null, 15, 30, TYPE_SEASONAL, "Grapes", null, LocalDate.now().plusDays(180),
                                LocalDate.now().plusDays(240)));
                return products;
        }
}
