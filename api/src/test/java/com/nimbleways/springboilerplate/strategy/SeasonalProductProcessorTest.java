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
class SeasonalProductProcessorTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private SeasonalProductProcessor processor;

    @Test
    void shouldDecreaseStock_whenInSeasonAndAvailable() {
        Product product = new Product();
        product.setAvailable(2);
        product.setSeasonStartDate(LocalDate.now().minusDays(1));
        product.setSeasonEndDate(LocalDate.now().plusDays(1));

        processor.process(product);

        assertEquals(1, product.getAvailable());
        verify(productRepository).save(product);
    }

    @Test
    void shouldHandleSeasonalProduct_whenOutOfSeason() {
        Product product = new Product();
        product.setAvailable(5);
        product.setSeasonStartDate(LocalDate.now().minusDays(10));
        product.setSeasonEndDate(LocalDate.now().minusDays(5));

        processor.process(product);

        verify(productService).handleSeasonalProduct(product);
        verifyNoInteractions(productRepository);
    }
}

