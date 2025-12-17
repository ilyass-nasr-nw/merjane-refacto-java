package com.nimbleways.springboilerplate.strategy;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ExpirableProductProcessorTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ExpirableProductProcessor processor;

    @Test
    void shouldDecreaseStock_whenNotExpiredAndAvailable() {
        Product product = new Product();
        product.setAvailable(3);
        product.setExpiryDate(LocalDate.now().plusDays(5));

        processor.process(product);

        assertEquals(2, product.getAvailable());
        verify(productRepository).save(product);
    }

    @Test
    void shouldHandleExpiredProduct_whenExpired() {
        Product product = new Product();
        product.setAvailable(3);
        product.setExpiryDate(LocalDate.now().minusDays(1));

        processor.process(product);

        verify(productService).handleExpiredProduct(product);
        verifyNoInteractions(productRepository);
    }
}

