package com.nimbleways.springboilerplate.services.product;

import com.nimbleways.springboilerplate.entities.NormalProduct;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.services.product.factory.ProductStrategyFactory;
import com.nimbleways.springboilerplate.services.product.strategy.HandleProductStrategy;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

@UnitTest
public class ProductServiceTest {


    @Mock
    private ProductStrategyFactory productStrategyFactory;

    @Mock
    private HandleProductStrategy handleProductStrategy;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldProcessProductUsingCorrectStrategy() {
        // GIVEN
        NormalProduct product = new NormalProduct(1L, 10, 5, ProductType.NORMAL, "Test Product");
        when(productStrategyFactory.getStrategy(product)).thenReturn(handleProductStrategy);

        // WHEN
        productService.processProduct(product);

        // THEN
        verify(productStrategyFactory, times(1)).getStrategy(product);
        verify(handleProductStrategy, times(1)).handleProduct(product);
    }

}
