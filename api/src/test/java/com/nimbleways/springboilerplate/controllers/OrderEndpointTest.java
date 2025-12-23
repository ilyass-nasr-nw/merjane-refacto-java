package com.nimbleways.springboilerplate.controllers;


import com.nimbleways.springboilerplate.constants.ProductType;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    private List<Product> sampleProducts;

    @BeforeEach
    void initializeTestData() {
        sampleProducts = createSampleProducts();
        productRepository.saveAll(sampleProducts);
    }

    @Test
    public void shouldDecrementProductAvailabilityWhenOrderProcessed() throws Exception {
        Product product = sampleProducts.get(0);
        product.setAvailable(8);
        product = productRepository.save(product);

        Set<Product> orderItems = new HashSet<>();
        orderItems.add(product);

        Order order = new Order();
        order.setItems(orderItems);
        order = orderRepository.save(order);

        mockMvc.perform(post("/orders/{orderId}/process", order.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk());

        Product updatedProduct = productRepository.findById(product.getId()).get();
        assertEquals(7, updatedProduct.getAvailable());
    }

    @Test
    public void shouldReturn404ForNonExistentOrder() throws Exception {
        mockMvc.perform(post("/orders/{orderId}/process", 12345)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldProcessMultipleProductsInOrder() throws Exception {
        Product p1 = sampleProducts.get(0);
        Product p2 = sampleProducts.get(1);
        p1.setAvailable(5);
        p2.setAvailable(3);
        productRepository.saveAll(List.of(p1, p2));

        Order order = new Order();
        order.setItems(new HashSet<>(List.of(p1, p2)));
        order = orderRepository.save(order);

        mockMvc.perform(post("/orders/{orderId}/process", order.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk());

        assertEquals(4, productRepository.findById(p1.getId()).get().getAvailable());
        assertEquals(2, productRepository.findById(p2.getId()).get().getAvailable());
    }

    @Test
    public void shouldNotDecrementExpiredProduct() throws Exception {
        Product expired = sampleProducts.get(3); 
        expired.setAvailable(10);
        productRepository.save(expired);

        Order order = new Order();
        order.setItems(Set.of(expired));
        order = orderRepository.save(order);

        mockMvc.perform(post("/orders/{orderId}/process", order.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk());

        assertEquals(0, productRepository.findById(expired.getId()).get().getAvailable());
    }

    @Test
    public void shouldHandleEmptyOrderGracefully() throws Exception {
        Order emptyOrder = new Order();
        emptyOrder.setItems(Collections.emptySet());
        emptyOrder = orderRepository.save(emptyOrder);

        mockMvc.perform(post("/orders/{orderId}/process", emptyOrder.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk());

        assertEquals(0, emptyOrder.getItems().size());
    }

    private static List<Product> createSampleProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(null, 20, 50, ProductType.NORMAL, "Tomat", null, null, null));
        products.add(new Product(null, 5, 10, ProductType.NORMAL, "Milk", null, null, null));
        products.add(new Product(null, 30, 60, ProductType.EXPIRABLE, "Cheese", LocalDate.now().plusDays(15), null, null));
        products.add(new Product(null, 50, 100, ProductType.EXPIRABLE, "Yogurt", LocalDate.now().minusDays(1), null, null));
        products.add(new Product(null, 25, 40, ProductType.SEASONAL, "Olive oil", null, LocalDate.now().minusDays(5), LocalDate.now().plusDays(20)));
        products.add(new Product(null, 10, 20, ProductType.SEASONAL, "Blueberries", null, LocalDate.now().plusDays(10), LocalDate.now().plusDays(50)));
        return products;
    }
}