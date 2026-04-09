package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.enums.ProductType;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@UnitTest
public class ProductServiceImplTest {

    @Mock
    private com.nimbleways.springboilerplate.services.implementations.NormalProductHandler normalProductHandler;

    @Mock
    private com.nimbleways.springboilerplate.services.implementations.SeasonalProductHandler seasonalProductHandler;

    @Mock
    private com.nimbleways.springboilerplate.services.implementations.ExpiredProductHandler expiredProductHandler;

    @InjectMocks
    private com.nimbleways.springboilerplate.services.implementations.ProductServiceImpl productService;

    @Test
    void processShouldDelegateNormalProductToNormalHandler() {
        Product product = new Product(1L, 5, 2, ProductType.NORMAL, "item", null, null, null);

        productService.process(Set.of(product));

        verify(normalProductHandler, times(1)).handle(product);
        verifyNoInteractions(seasonalProductHandler, expiredProductHandler);
    }

    @Test
    void processShouldDelegateSeasonalProductToSeasonalHandler() {
        Product product = new Product(2L, 3, 1, ProductType.SEASONAL, "seasonal", null, null, null);

        productService.process(Set.of(product));

        verify(seasonalProductHandler, times(1)).handle(product);
        verifyNoInteractions(normalProductHandler, expiredProductHandler);
    }

    @Test
    void processShouldDelegateExpirableProductToExpiredHandler() {
        Product product = new Product(3L, 7, 0, ProductType.EXPIRABLE, "perishable", null, null, null);

        productService.process(Set.of(product));

        verify(expiredProductHandler, times(1)).handle(product);
        verifyNoInteractions(normalProductHandler, seasonalProductHandler);
    }

    @Test
    void processShouldRouteEachProductInSetToMatchingHandler() {
        Product normal = new Product(1L, 1, 1, ProductType.NORMAL, "n", null, null, null);
        Product seasonal = new Product(2L, 1, 1, ProductType.SEASONAL, "s", null, null, null);
        Product expirable = new Product(3L, 1, 1, ProductType.EXPIRABLE, "e", null, null, null);

        productService.process(Set.of(normal, seasonal, expirable));

        verify(normalProductHandler, times(1)).handle(normal);
        verify(seasonalProductHandler, times(1)).handle(seasonal);
        verify(expiredProductHandler, times(1)).handle(expirable);
    }

    @Test
    void processWithEmptySetShouldNotInvokeHandlers() {
        productService.process(Set.of());

        verifyNoInteractions(normalProductHandler, seasonalProductHandler, expiredProductHandler);
    }
}
