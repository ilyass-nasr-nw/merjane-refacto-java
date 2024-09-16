package com.nimbleways.springboilerplate.services.strategies.product;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.helpers.ProductHelper;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.NotificationService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;


@UnitTest
@ExtendWith(SpringExtension.class)
class SeasonalProductStrategyTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SeasonalProductStrategy strategy;


    private Product product;



    @Test
    void testProcessAvailableProductInSeasonWillUpdateAvailability() {

        //given
        HashMap<String, Object> productValues = new HashMap<>();
        productValues.put("seasonStartDate", LocalDate.now().minusDays(1));
        productValues.put("seasonEndDate", LocalDate.now().plusMonths(1));
        var product = ProductHelper.createProduct(ProductHelper.defaultProductValues(productValues));

        int availability = product.getAvailable();

        //when
        strategy.process(product);

        //then
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService,Mockito.never()).sendDelayNotification(product.getLeadTime(), product.getName());

        // check that product availability has decreased by one element
        Assertions.assertEquals(availability-1, product.getAvailable());
    }



}