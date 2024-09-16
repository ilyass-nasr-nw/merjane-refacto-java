package com.nimbleways.springboilerplate.services.strategies.product;

import com.nimbleways.springboilerplate.helpers.ProductHelper;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.NotificationService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.HashMap;


@UnitTest
@ExtendWith(SpringExtension.class)
class ExpirableProductStrategyTest {


    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ExpirableProductStrategy strategy;



    @Test
    void testProcessAvailableProductWillUpdateAvailability() {

        //given
        var product = ProductHelper.createProduct(ProductHelper.defaultProductValues(new HashMap<>()));

        int availability = product.getAvailable();

        //when
        strategy.process(product);

        //then
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService,Mockito.never()).sendDelayNotification(product.getLeadTime(), product.getName());

        // check that product availability has decreased by one element
        Assertions.assertEquals(availability-1, product.getAvailable());

    }


    @Test
    void testProcessUnavailableProductWillSendDExpirationNotificationAndInitStockIfProductIsNotAvailable() {

        //given
        HashMap<String, Object> productValues = new HashMap<>();
        productValues.put("available", 0);
        var product = ProductHelper.createProduct(ProductHelper.defaultProductValues(productValues));

        //when
        strategy.process(product);

        //then
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService,Mockito.times(1)).sendExpirationNotification(product.getName(), product.getExpiryDate());

        // check that product availability has decreased by one element
        Assertions.assertEquals(0, product.getAvailable());
    }


    @Test
    void testProcessUnavailableProductWillSendDExpirationNotificationAndInitStockIfProductIsExpired() {

        //given
        HashMap<String, Object> productValues = new HashMap<>();
        productValues.put("expiryDate", LocalDate.now().minusDays(3));
        var product = ProductHelper.createProduct(ProductHelper.defaultProductValues(productValues));

        //when
        strategy.process(product);

        //then
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService,Mockito.times(1)).sendExpirationNotification(product.getName(), product.getExpiryDate());

        // check that product availability has decreased by one element
        Assertions.assertEquals(0, product.getAvailable());
    }

}