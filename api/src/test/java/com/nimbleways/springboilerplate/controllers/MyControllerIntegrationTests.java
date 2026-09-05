package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Walks a basket containing one product per business case through the real
 * endpoint and checks what ended up in the database.
 */
@SpringBootTest
@AutoConfigureMockMvc
class MyControllerIntegrationTests {

    private static final LocalDate TODAY = LocalDate.now();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @MockBean
    private NotificationService notificationService;

    @BeforeEach
    void cleanDatabase() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("processing an order applies the right rule to every product of the basket")
    void processOrderAppliesTheRuleOfEachProductType() throws Exception {
        List<Product> basket = List.of(
                availableNormalProduct(),
                outOfStockNormalProduct(),
                outOfStockNormalProductWithoutLeadTime(),
                freshExpirableProduct(),
                expiredProduct(),
                inSeasonProduct(),
                productWhoseSeasonHasNotStarted(),
                outOfStockProductRestockedAfterSeasonEnd(),
                outOfStockProductRestockedBeforeSeasonEnd());
        Order order = saveOrderContaining(basket);

        mockMvc.perform(post("/orders/{orderId}/processOrder", order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()));

        // NORMAL
        assertThat(stockOf("USB Cable")).isEqualTo(29);
        assertThat(stockOf("USB Dongle")).isZero();
        verify(notificationService).sendDelayNotification(10, "USB Dongle");
        assertThat(stockOf("Compass")).isZero();
        verify(notificationService, never()).sendDelayNotification(0, "Compass");

        // EXPIRABLE
        assertThat(stockOf("Butter")).isEqualTo(29);
        assertThat(stockOf("Milk")).isZero();
        verify(notificationService).sendExpirationNotification("Milk", TODAY.minusDays(2));

        // SEASONAL
        assertThat(stockOf("Watermelon")).isEqualTo(29);
        // Out of season but still in stock: the customer is told it is unavailable
        // and the stock is deliberately left alone.
        assertThat(stockOf("Grapes")).isEqualTo(30);
        verify(notificationService).sendOutOfStockNotification("Grapes");
        assertThat(stockOf("Pumpkin")).isZero();
        verify(notificationService).sendOutOfStockNotification("Pumpkin");
        assertThat(stockOf("Strawberry")).isZero();
        verify(notificationService).sendDelayNotification(5, "Strawberry");
    }

    private Order saveOrderContaining(List<Product> products) {
        productRepository.saveAll(products);
        Order order = new Order();
        order.setItems(Set.copyOf(products));
        return orderRepository.save(order);
    }

    private int stockOf(String productName) {
        return productRepository.findFirstByName(productName)
                .orElseThrow(() -> new AssertionError(productName + " is missing from the database"))
                .getAvailable();
    }

    private static Product availableNormalProduct() {
        return Product.builder().type(ProductType.NORMAL).name("USB Cable")
                .available(30).leadTime(15).build();
    }

    private static Product outOfStockNormalProduct() {
        return Product.builder().type(ProductType.NORMAL).name("USB Dongle")
                .available(0).leadTime(10).build();
    }

    private static Product outOfStockNormalProductWithoutLeadTime() {
        return Product.builder().type(ProductType.NORMAL).name("Compass")
                .available(0).leadTime(0).build();
    }

    private static Product freshExpirableProduct() {
        return Product.builder().type(ProductType.EXPIRABLE).name("Butter")
                .available(30).leadTime(15).expiryDate(TODAY.plusDays(26)).build();
    }

    private static Product expiredProduct() {
        return Product.builder().type(ProductType.EXPIRABLE).name("Milk")
                .available(6).leadTime(90).expiryDate(TODAY.minusDays(2)).build();
    }

    private static Product inSeasonProduct() {
        return Product.builder().type(ProductType.SEASONAL).name("Watermelon")
                .available(30).leadTime(15)
                .seasonStartDate(TODAY.minusDays(2)).seasonEndDate(TODAY.plusDays(58)).build();
    }

    private static Product productWhoseSeasonHasNotStarted() {
        return Product.builder().type(ProductType.SEASONAL).name("Grapes")
                .available(30).leadTime(15)
                .seasonStartDate(TODAY.plusDays(180)).seasonEndDate(TODAY.plusDays(240)).build();
    }

    private static Product outOfStockProductRestockedAfterSeasonEnd() {
        return Product.builder().type(ProductType.SEASONAL).name("Pumpkin")
                .available(0).leadTime(15)
                .seasonStartDate(TODAY.minusDays(30)).seasonEndDate(TODAY.plusDays(5)).build();
    }

    private static Product outOfStockProductRestockedBeforeSeasonEnd() {
        return Product.builder().type(ProductType.SEASONAL).name("Strawberry")
                .available(0).leadTime(5)
                .seasonStartDate(TODAY.minusDays(10)).seasonEndDate(TODAY.plusDays(30)).build();
    }
}
