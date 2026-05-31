package com.nimbleways.springboilerplate.services.implementations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.nimbleways.springboilerplate.contollers.MyController;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

@ExtendWith(MockitoExtension.class)
class MyControllerUnitTest {

    @InjectMocks
    private MyController controller;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private NotificationService notificationService;

    private Order createOrder(Product product) {
        Order order = new Order();
        order.setId(1L);
        order.setItems(Set.of(product));
        return order;
    }
    
    // =====================================================
    //NORMAL : stock disponible
    
    
    @Test
    void shouldDecrementAvailableForNormalProduct() {

        Product product = new Product(
                1L,
                30,
                15,
                "NORMAL",
                "USB Cable",
                null,
                null,
                null);

        Order order = createOrder(product);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        controller.processOrder(1L);

        assertEquals(14, product.getAvailable());

        verify(productRepository).save(product);
    }
    
    
    // =====================================================
    //NORMAL : rupture de stock stock épuisé
    @Test
    void shouldNotifyDelayWhenNormalProductOutOfStock() {

        Product product = new Product(
                1L,
                10
                0,
                "NORMAL",
                "USB Dongle",
                null,
                null,
                null);

        Order order = createOrder(product);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        controller.processOrder(1L);

        verify(notificationService)
                .sendDelayNotification(0, "USB Dongle");

        verify(productRepository, never()).save(any());
    }
    
    // =====================================================
    //EXPIRABLE valide
    
    @Test
    void shouldSellNonExpiredProduct() {

        Product product = new Product(
                1L,
                30,
                15,
                "EXPIRABLE",
                "Butter",
                LocalDate.now().plusDays(10),
                null,
                null);

        Order order = createOrder(product);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        controller.processOrder(1L);

        assertEquals(14, product.getAvailable());

        verify(productRepository).save(product);
    }
	
	 // =====================================================
    //EXPIRABLE périmé ou expiré
    
    @Test
    void shouldHandleExpiredProduct() {

        Product product = new Product(
                1L,
                30,
                15,
                "EXPIRABLE",
                "Milk",
                LocalDate.now().minusDays(2),
                null,
                null);

        Order order = createOrder(product);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        controller.processOrder(1L);

        verify(notificationService)
                .sendExpirationNotification(
                        eq("Milk"),
                        eq(product.getExpiryDate()));

        verify(productRepository, never()).save(any());
    }
    
    
    // =====================================================
    //SEASONAL pendant la saison
    @Test
    void shouldSellSeasonalProductDuringSeason() {

        Product product = new Product(
                1L,
                30,
                15,
                "SEASONAL",
                "Watermelon",
                null,
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(10));

        Order order = createOrder(product);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        controller.processOrder(1L);

        assertEquals(14, product.getAvailable());

        verify(productRepository).save(product);
    }
    
    // =====================================================
    //SEASONAL hors saison
    
    @Test
    void shouldNotSellSeasonalProductOutsideSeason() {

        Product product = new Product(
                1L,
                30,
                15,
                "SEASONAL",
                "Grapes",
                null,
                LocalDate.now().plusDays(180),
                LocalDate.now().plusDays(240));

        Order order = createOrder(product);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        controller.processOrder(1L);

        verify(notificationService)
                .sendOutOfStockNotification("Grapes");

        verify(productRepository, never()).save(any());
    }
}
