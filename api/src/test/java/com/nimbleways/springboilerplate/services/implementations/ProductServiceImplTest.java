package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.product.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.services.product.ProductService;
import com.nimbleways.springboilerplate.services.implementations.factory.ProcessProductStrategyFactory;
import com.nimbleways.springboilerplate.services.product.ProcessProductStrategy;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@UnitTest
class ProductServiceImplTest {
    private ProcessProductStrategyFactory strategyFactory;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        strategyFactory = mock(ProcessProductStrategyFactory.class);
        productService = new ProductServiceImpl(strategyFactory);
    }

    @Test
    void processProduct_shouldDelegateToStrategy_andReturnProcessedProduct() {

        Product inputProduct = mock(Product.class);
        Product processedProduct = mock(Product.class);

        ProductType type = ProductType.SEASONAL;
        ProcessProductStrategy strategy = mock(ProcessProductStrategy.class);

        when(inputProduct.getName()).thenReturn("Laptop");
        when(inputProduct.getType()).thenReturn(type);
        when(strategyFactory.getProductStrategy(type)).thenReturn(strategy);
        when(strategy.processProduct(inputProduct)).thenReturn(processedProduct);

        Product result = productService.processProduct(inputProduct);

        assertThat(result).isEqualTo(processedProduct);

        verify(strategyFactory, times(1)).getProductStrategy(type);
        verify(strategy, times(1)).processProduct(inputProduct);
    }

    @Test
    void processProduct_shouldThrow_whenStrategyNotFound() {

        Product product = mock(Product.class);
        when(product.getType()).thenReturn(null);

        when(strategyFactory.getProductStrategy(null))
                .thenThrow(new IllegalArgumentException("No strategy found"));

        assertThatThrownBy(() -> productService.processProduct(product))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No strategy found");

        verify(strategyFactory).getProductStrategy(null);
    }

    @Test
    void processProduct_shouldReturnNull_ifStrategyReturnsNull() {

        Product product = mock(Product.class);
        ProductType type = ProductType.NORMAL;

        ProcessProductStrategy strategy = mock(ProcessProductStrategy.class);

        when(product.getType()).thenReturn(type);
        when(strategyFactory.getProductStrategy(type)).thenReturn(strategy);
        when(strategy.processProduct(product)).thenReturn(null);

        Product result = productService.processProduct(product);

        assertThat(result).isNull();

        verify(strategyFactory).getProductStrategy(type);
        verify(strategy).processProduct(product);
    }
}
