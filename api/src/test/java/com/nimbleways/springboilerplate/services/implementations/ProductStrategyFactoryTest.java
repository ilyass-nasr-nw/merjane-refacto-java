package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.services.implementations.exceptions.UnsupportedProductTypeException;
import com.nimbleways.springboilerplate.services.implementations.strategies.ProductStrategy;
import com.nimbleways.springboilerplate.services.implementations.strategies.ProductStrategyFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductStrategyFactoryTest {

    @Test
    void shouldReturnStrategyByProductType() {
        ProductStrategy normalStrategy = mock(ProductStrategy.class);
        when(normalStrategy.getProductType()).thenReturn("NORMAL");

        ProductStrategyFactory factory = new ProductStrategyFactory(List.of(normalStrategy));

        ProductStrategy result = factory.getProductStrategy("NORMAL");

        assertSame(normalStrategy, result);
    }

    @Test
    void shouldThrowExceptionWhenProductTypeIsUnsupported() {
        ProductStrategyFactory factory = new ProductStrategyFactory(List.of());

        UnsupportedProductTypeException unsupportedProductTypeException = assertThrows(
                UnsupportedProductTypeException.class,
                () -> factory.getProductStrategy("UNKNOWN")
        );

        assertEquals("unsupported product type : UNKNOWN",unsupportedProductTypeException.getMessage());
    }
}
