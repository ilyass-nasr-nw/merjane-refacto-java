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

import static org.junit.Assert.assertEquals;

// import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                mockMvc.perform(post("/orders/{orderId}/processOrder", order.getId())
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
            
            // Produit NORMAL avec stock
            Product p1 = new Product();
            p1.setLeadTime(15);
            p1.setAvailable(30);
            p1.setType("NORMAL");
            p1.setName("USB Cable");
            products.add(p1);
            
            // Produit NORMAL sans stock
            Product p2 = new Product();
            p2.setLeadTime(10);
            p2.setAvailable(0);
            p2.setType("NORMAL");
            p2.setName("USB Dongle");
            products.add(p2);
            
            // Produit EXPIRABLE non expiré
            Product p3 = new Product();
            p3.setLeadTime(15);
            p3.setAvailable(30);
            p3.setType("EXPIRABLE");
            p3.setName("Butter");
            p3.setExpiryDate(LocalDate.now().plusDays(26));
            products.add(p3);
            
            // Produit EXPIRABLE expiré
            Product p4 = new Product();
            p4.setLeadTime(90);
            p4.setAvailable(6);
            p4.setType("EXPIRABLE");
            p4.setName("Milk");
            p4.setExpiryDate(LocalDate.now().minusDays(2));
            products.add(p4);
            
            // Produit SEASONAL en saison
            Product p5 = new Product();
            p5.setLeadTime(15);
            p5.setAvailable(30);
            p5.setType("SEASONAL");
            p5.setName("Watermelon");
            p5.setSeasonStartDate(LocalDate.now().minusDays(2));
            p5.setSeasonEndDate(LocalDate.now().plusDays(58));
            products.add(p5);
            
            // Produit SEASONAL hors saison
            Product p6 = new Product();
            p6.setLeadTime(15);
            p6.setAvailable(30);
            p6.setType("SEASONAL");
            p6.setName("Grapes");
            p6.setSeasonStartDate(LocalDate.now().plusDays(180));
            p6.setSeasonEndDate(LocalDate.now().plusDays(240));
            products.add(p6);
            
            return products;
        }

}
