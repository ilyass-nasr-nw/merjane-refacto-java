package com.nimbleways.merjane.controllers;

import com.nimbleways.merjane.persistence.entities.Order;
import com.nimbleways.merjane.persistence.entities.Product;
import com.nimbleways.merjane.domain.ProductType;
import com.nimbleways.merjane.persistence.repositories.OrderRepository;
import com.nimbleways.merjane.persistence.repositories.ProductRepository;
import com.nimbleways.merjane.services.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
    public void processOrderShouldReturn() throws Exception {
        List<Product> allProducts = createProducts();
        Order order = createOrder(allProducts);
        productRepository.saveAll(allProducts);
        order = orderRepository.save(order);

        mockMvc.perform(post("/orders/{orderId}/processOrder", order.getId())
                        .contentType("application/json"))
                        .andExpect(status().isOk());

        // NORMAL: available > 0 : decremented
        Product usbCable = productRepository.findFirstByName("USB Cable").get();
        Assertions.assertEquals(29, (int) usbCable.getAvailable());

        // NORMAL: available = 0, leadTime > 0 : delay notification sent
        Product usbDongle = productRepository.findFirstByName("USB Dongle").get();
        Assertions.assertEquals(0, (int) usbDongle.getAvailable());
        Mockito.verify(notificationService).sendDelayNotification(10, "USB Dongle");

        // EXPIRABLE: available > 0, not expired : decremented
        Product butter = productRepository.findFirstByName("Butter").get();
        Assertions.assertEquals(29, (int) butter.getAvailable());

        // EXPIRABLE: expired : available set to 0, expiration notification sent
        Product milk = productRepository.findFirstByName("Milk").get();
        Assertions.assertEquals(0, (int) milk.getAvailable());
        Mockito.verify(notificationService).sendExpirationNotification(eq("Milk"), any(LocalDate.class));

        // SEASONAL: in season, available > 0 : decremented
        Product watermelon = productRepository.findFirstByName("Watermelon").get();
        Assertions.assertEquals(29, (int) watermelon.getAvailable());

        // SEASONAL: season not started : out-of-stock notification, available unchanged
        Product grapes = productRepository.findFirstByName("Grapes").get();
        Assertions.assertEquals(30, (int) grapes.getAvailable());
        Mockito.verify(notificationService).sendOutOfStockNotification("Grapes");

        // NORMAL: available = 0, leadTime = 0 : nothing happens
        Product rj45Cable = productRepository.findFirstByName("RJ45 Cable").get();
        Assertions.assertEquals(0, (int) rj45Cable.getAvailable());
        Mockito.verify(notificationService, Mockito.never()).sendDelayNotification(eq(0), eq("RJ45 Cable"));

        // SEASONAL: in season, available = 0 : delay notification
        Product mango = productRepository.findFirstByName("Mango").get();
        Assertions.assertEquals(0, (int) mango.getAvailable());
        Mockito.verify(notificationService).sendDelayNotification(5, "Mango");

        // SEASONAL: season ended : out-of-stock notification, available set to 0
        Product strawberry = productRepository.findFirstByName("Strawberry").get();
        Assertions.assertEquals(0, (int) strawberry.getAvailable());
        Mockito.verify(notificationService).sendOutOfStockNotification("Strawberry");

        // EXPIRABLE: available = 0, not expired : expiration notification, available stays 0
        Product cheese = productRepository.findFirstByName("Cheese").get();
        Assertions.assertEquals(0, (int) cheese.getAvailable());
        Mockito.verify(notificationService).sendExpirationNotification(eq("Cheese"), any(LocalDate.class));
    }

    private static Order createOrder(List<Product> products) {
        Order order = new Order();
        order.setItems(new HashSet<>(products));
        return order;
    }

    private static List<Product> createProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(null, 15, 30, ProductType.NORMAL, "USB Cable", null, null, null));
        products.add(new Product(null, 10, 0, ProductType.NORMAL, "USB Dongle", null, null, null));
        products.add(new Product(null, 15, 30, ProductType.EXPIRABLE, "Butter", LocalDate.now().plusDays(26), null, null));
        products.add(new Product(null, 90, 6, ProductType.EXPIRABLE, "Milk", LocalDate.now().minusDays(2), null, null));
        products.add(new Product(null, 15, 30, ProductType.SEASONAL, "Watermelon", null, LocalDate.now().minusDays(2), LocalDate.now().plusDays(58)));
        products.add(new Product(null, 15, 30, ProductType.SEASONAL, "Grapes", null, LocalDate.now().plusDays(180), LocalDate.now().plusDays(240)));
        // NORMAL: available = 0, leadTime = 0 : nothing happens
        products.add(new Product(null, 0, 0, ProductType.NORMAL, "RJ45 Cable", null, null, null));
        // SEASONAL: in season, available = 0 : delay notification
        products.add(new Product(null, 5, 0, ProductType.SEASONAL, "Mango", null, LocalDate.now().minusDays(2), LocalDate.now().plusDays(58)));
        // SEASONAL: season ended : out-of-stock notification
        products.add(new Product(null, 1, 5, ProductType.SEASONAL, "Strawberry", null, LocalDate.now().minusDays(100), LocalDate.now().minusDays(10)));
        // EXPIRABLE: available = 0, not expired : expiration notification
        products.add(new Product(null, 15, 0, ProductType.EXPIRABLE, "Cheese", LocalDate.now().plusDays(30), null, null));
        return products;
    }
}
