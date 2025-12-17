package com.nimbleways.springboilerplate.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ProductProcessorFactoryTest {

    @Mock
    private NormalProductProcessor normalProcessor;

    @Mock
    private SeasonalProductProcessor seasonalProcessor;

    @Mock
    private ExpirableProductProcessor expirableProcessor;

    private ProductProcessorFactory factory;

    @BeforeEach
    void setUp() {
        factory = new ProductProcessorFactory(
                normalProcessor,
                seasonalProcessor,
                expirableProcessor
        );
    }

    @Test
    void shouldReturnNormalProcessor_whenTypeIsNormal() {
        ProductProcessor processor = factory.getProcessor("NORMAL");

        assertSame(normalProcessor, processor);
    }

    @Test
    void shouldReturnSeasonalProcessor_whenTypeIsSeasonal() {
        ProductProcessor processor = factory.getProcessor("SEASONAL");

        assertSame(seasonalProcessor, processor);
    }

    @Test
    void shouldReturnExpirableProcessor_whenTypeIsExpirable() {
        ProductProcessor processor = factory.getProcessor("EXPIRABLE");

        assertSame(expirableProcessor, processor);
    }

    @Test
    void shouldThrowException_whenTypeIsUnknown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> factory.getProcessor("UNKNOWN")
        );

        assertEquals("Unknown product type: UNKNOWN", exception.getMessage());
    }
}
