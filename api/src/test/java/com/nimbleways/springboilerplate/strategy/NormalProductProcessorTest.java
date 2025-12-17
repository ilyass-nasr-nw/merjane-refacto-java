package com.nimbleways.springboilerplate.strategy;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class NormalProductProcessorTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private NormalProductProcessor processor;

    @Test
    void shouldDecreaseStock_whenProductIsAvailable() {
        Product product = new Product();
        product.setAvailable(5);
        product.setLeadTime(0);

        processor.process(product);

        assertEquals(4, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(productService);
    }

    @Test
    void shouldNotifyDelay_whenNoStockAndLeadTimeExists() {
        Product product = new Product();
        product.setAvailable(0);
        product.setLeadTime(3);

        processor.process(product);

        verify(productService).notifyDelay(3, product);
        verifyNoInteractions(productRepository);
    }
}

