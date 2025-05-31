package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enumerations.ProductTypeEnum;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@UnitTest
class ProductServiceTest {

    @Mock
    private NotificationService notificationService;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    @Test
    void testNotifyDelayWithNullLeadTime() {
        // GIVEN
        LocalDate now = LocalDate.now();
        Product product = new Product(1, 0, 0, ProductTypeEnum.NORMAL, "RJ45 Cable", null, null, null);
        List<Product> products = new ArrayList<>();

        doNothing().when(notificationService).sendDelayNotification(Mockito.anyInt(), Mockito.anyString());

        // WHEN
        productService.notifyDelay(product.getLeadTime(), product, products);

        // THEN
        Mockito.verify(notificationService, times(1)).sendDelayNotification(Mockito.anyInt(), Mockito.anyString());
    }

    @Test
    void testProcessNormalProduct() {
        // GIVEN
        Product product = new Product(1, 0, 1, ProductTypeEnum.NORMAL, "RJ45 Cable", null, null, null);
        List<Product> products = new ArrayList<>();

        // WHEN
        productService.processNormalProduct(product, products);

        // THEN
        // verify that it would never call the delay notification method when product is available
        Mockito.verify(notificationService, never()).sendDelayNotification(Mockito.anyInt(), Mockito.anyString());
    }

    @Test
    void testProcessNormalProduct() {
        // GIVEN
        Product product = new Product(1, 1, 0, ProductTypeEnum.NORMAL, "RJ45 Cable", null, null, null);
        List<Product> products = new ArrayList<>();

        // WHEN
        productService.processNormalProduct(product, products);

        // THEN
        // verify that it would call the delay notification method when product is unavailable
        Mockito.verify(notificationService, times(1)).sendDelayNotification(Mockito.anyInt(), Mockito.anyString());
    }

    @Test
    void testProcessExpirableProduct() {
        // GIVEN
        LocalDate now = LocalDate.now();
        Product product = new Product(1, 0, 1, ProductTypeEnum.EXPIRABLE, "Milk", now.plusDays(30), null, null);
        List<Product> products = new ArrayList<>();

        // WHEN
        productService.processExpirableProduct(product, products);

        // THEN
        // verify that it would never call the expiration notification method for available products that are not expired
        Mockito.verify(notificationService, never()).sendExpirationNotification(Mockito.anyInt(), Mockito.anyString());
    }

    @Test
    void testProcessExpirableProduct() {
        // GIVEN
        LocalDate now = LocalDate.now();
        Product product = new Product(1, 0, 1, ProductTypeEnum.EXPIRABLE, "Milk", now.minusDays(30), null, null);
        List<Product> products = new ArrayList<>();

        // WHEN
        productService.processExpirableProduct(product, products);

        // THEN
        // verify that it would call the expiration notification method for products that are either unavailable or expired
        Mockito.verify(notificationService, times(1)).sendExpirationNotification(Mockito.anyInt(), Mockito.anyString());
    }

    @Test
    void testProcessSeasonalProduct() {
        // GIVEN
        LocalDate now = LocalDate.now();
        Product product = new Product(1, 0, 1, ProductTypeEnum.SEASONAL, "Okra", now.plusDays(2), now.minusDays(30), now.minusDays(30));
        List<Product> products = new ArrayList<>();

        // WHEN
        productService.processSeasonalProduct(product, products);

        // THEN
        // verify that it would never call the out of stock notification method for available products that are in season
        Mockito.verify(notificationService, never()).sendOutOfStockNotification(Mockito.anyInt(), Mockito.anyString());
    }
}