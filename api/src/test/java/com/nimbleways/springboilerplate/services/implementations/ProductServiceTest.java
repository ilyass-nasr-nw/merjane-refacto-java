package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enumerations.ProductTypeEnum;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@UnitTest
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private List<Product> productsToSave;

    @BeforeEach
    void setUp() {
        product = new Product(null, 15, 0, ProductTypeEnum.NORMAL, "RJ45 Cable", null, null, null);
        productsToSave = new ArrayList<>();
    }

    @Test
    void testHandleNormalProduct_Available() {
        product.setAvailable(10);
        productService.handleNormalProduct(product, productsToSave);
        assertEquals(9, product.getAvailable());
        assertEquals(1, productsToSave.size());
    }

    @Test
    void testHandleNormalProduct_NotAvailable() {
        product.setAvailable(0);
        productService.handleNormalProduct(product, productsToSave);
        verify(notificationService, times(1)).sendDelayNotification(product.getLeadTime(), product.getName());
    }

    @Test
    void testHandleSeasonalProduct_AvailableInSeason() {
        product.setAvailable(10);
        product.setSeasonStartDate(LocalDate.now().minusDays(1));
        product.setSeasonEndDate(LocalDate.now().plusDays(1));
        productService.handleSeasonalProduct(product, productsToSave);
        assertEquals(9, product.getAvailable());
        assertEquals(1, productsToSave.size());
    }

    @Test
    void testHandleSeasonalProduct_NotAvailableInSeason() {
        product.setAvailable(0);
        product.setLeadTime(15);
        product.setSeasonStartDate(LocalDate.now().minusDays(1));
        product.setSeasonEndDate(LocalDate.now().plusDays(1));
        productService.handleSeasonalProduct(product, productsToSave);
        verify(notificationService, times(1)).sendOutOfStockNotification(product.getName());
    }

    @Test
    void testHandleExpiredProduct_AvailableNotExpired() {
        product.setAvailable(10);
        product.setExpiryDate(LocalDate.now().plusDays(1));
        productService.handleExpiredProduct(product, productsToSave);
        assertEquals(9, product.getAvailable());
        assertEquals(1, productsToSave.size());
    }

    @Test
    void testHandleExpiredProduct_NotAvailableOrExpired() {
        product.setAvailable(0);
        product.setExpiryDate(LocalDate.now().minusDays(1));
        productService.handleExpiredProduct(product, productsToSave);
        verify(notificationService, times(1)).sendExpirationNotification(product.getName(), product.getExpiryDate());
    }

    @Test
    void testUpdateProducts() {
        List<Product> products = new ArrayList<>();
        products.add(product);
        productService.updateProducts(products);
        verify(productRepository, times(1)).saveAll(products);
    }
}