package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.entities.order.Order;
import com.nimbleways.springboilerplate.entities.product.ExpirableProduct;
import com.nimbleways.springboilerplate.entities.product.NormalProduct;
import com.nimbleways.springboilerplate.entities.product.Product;
import com.nimbleways.springboilerplate.entities.product.SeasonalProduct;
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
public class OrderApiIntegrationTests {
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
        products.add(NormalProduct.builder()
                .leadTime(15)
                .available(30)
                .type(ProductType.NORMAL)
                .name("USB Cable")
                .build());
        products.add(NormalProduct.builder()
                .leadTime(10)
                .available(0)
                .type(ProductType.NORMAL)
                .name("USB Dongle")
                .build());
        products.add(ExpirableProduct.builder()
                .leadTime(15)
                .available(30)
                .type(ProductType.EXPIRABLE)
                .name("Butter")
                .expiryDate(LocalDate.now().plusDays(26))
                .build());

        products.add(ExpirableProduct.builder()
                .leadTime(90)
                .available(6)
                .type(ProductType.EXPIRABLE)
                .name("Milk")
                .expiryDate(LocalDate.now().minusDays(2))
                .build());
        products.add(SeasonalProduct.builder()
                .leadTime(15)
                .available(30)
                .type(ProductType.SEASONAL)
                .name("Watermelon")
                .seasonStartDate(LocalDate.now().minusDays(2))
                .seasonEndDate(LocalDate.now().plusDays(58))
                .build());
        products.add(SeasonalProduct.builder()
                .leadTime(15)
                .available(30)
                .type(ProductType.SEASONAL)
                .name("Grapes")
                .seasonStartDate(LocalDate.now().minusDays(180))
                .seasonEndDate(LocalDate.now().plusDays(240))
                .build());


        return products;
    }

}
