package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductTypeEnum;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.impl.NotificationService;
import com.nimbleways.springboilerplate.services.impl.ProductServiceImpl;
import com.nimbleways.springboilerplate.strategy.ExpirableProductProcessing;
import com.nimbleways.springboilerplate.strategy.NormalProductProcessing;
import com.nimbleways.springboilerplate.strategy.ProductProcessingStrategy;
import com.nimbleways.springboilerplate.strategy.SeasonalProductProcessing;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@UnitTest
public class MyUnitTests {

    @Mock
    ProductProcessingStrategy productProcessingStrategy;
    @Mock
    ProductRepository productRepository;

    @Test
    void normalProduct_decrementsStock() {
        Product product = Product.builder()
                .available(3)
                .type(ProductTypeEnum.NORMAL)
                .leadTime(9)
                .build();

        productProcessingStrategy.process(product);

        assertEquals(2, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(productProcessingStrategy);
    }
}