package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.services.ProductProcessor;
import com.nimbleways.springboilerplate.services.ProductProcessorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductProcessorFactoryTest {

    private ProductProcessorFactory factory;
    private ProductProcessor normalProcessor;
    private ProductProcessor seasonalProcessor;

    @BeforeEach
    void setUp() {
        normalProcessor = mock(NormalProductProcessor.class);
        when(normalProcessor.supports("NORMAL")).thenReturn(true);
        when(normalProcessor.supports("SEASONAL")).thenReturn(false);

        seasonalProcessor = mock(SeasonalProductProcessor.class);
        when(seasonalProcessor.supports("SEASONAL")).thenReturn(true);
        when(seasonalProcessor.supports("NORMAL")).thenReturn(false);

        factory = new ProductProcessorFactory(List.of(normalProcessor, seasonalProcessor));
    }

    @Test
    void shouldReturnCorrectProcessor() {
        assertEquals(normalProcessor, factory.getProcessor("NORMAL"));
        assertEquals(seasonalProcessor, factory.getProcessor("SEASONAL"));
    }

    @Test
    void shouldThrowExceptionForUnknownType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.getProcessor("UNKNOWN_TYPE");
        });
        
        assertTrue(exception.getMessage().contains("Unknown product type"));
    }
}